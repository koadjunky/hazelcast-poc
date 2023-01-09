# hazelcast-poc

Hazelcast proof-of-concept. Covers features:
 - management center
 - storing pojo and protobuf records
 - records versioning, compact serializer
 - local cache
 - sql queries
 - predicates and sql predicates
 - executors
 - indexes
 - Influxdb database backend
 - transactions
 - split-brain detection

## Building

To build project execute:

```./gradlew clean build docker```

To run project execute:

```docker compose up```

and then in separate windows:

```./gradlew :hazelcast-poc-client:bootRun```

and

```./gradlew :hazelcast-poc-client2:bootRun```

Swagger consoles of clients are available on ports 9000 and 9001.

## Docker Compose

Start docker compose:

```docker compose up```

Shut docker compose:

```docker compose down```

Shut docker compose and remove volumes:

```docker compose down -v```

## Hazelcast

Scale hazelcast cluster (management cluster works up to 2 nodes without license):

```docker compose up --scale hazelcast1=5```

Shut down hazelcast worker:

```docker compose stop hazelcast3```

Start hazelcast worker:

```docker compose start hazelcast3```

Access management center:

```http://localhost:8080/clusters/dev``` (admin/ChangeM3)

## InfluxDB

InfluxDB console is available at:

```http://localhost:8086/``` (admin/adminuser)

Obtain token from running container:

```docker exec hazelcast-poc-influxdb-1 influx auth list | awk '/admin/ {print $4 " "}'```

## Grafana

Grafana web interface is available at:

```http://localhost:3000/``` (admin/admin)

## Prometheus

Prometheus web interface is available at:

```http://localhost:9090```

# Performance notes

## Transactions

Load 100k of records into both protobuf and pojo maps:
* without transaction - 28s
* one phase transaction - 54s (x2)
* two phase transaction - 86s (x3)

## Task executor

Running query via task executor on three nodes, query selects 1/4 of records and
calculates sum of one field.

| Operation                | protobuf      | pojo       |
|--------------------------|---------------|------------|
| predicate with index     | 170ms         | 333ms      |
| predicate without index  | 553ms (x3.25) | 561ms (x2) |
| no predicate (full scan) | 1100ms (x6.5) | 867ms (x3) |

## Task executor vs SQL query

Map loaded with 100k records (both protobuf and pojo at the same time). Query selects 1/4
of records and calculates sum of one field. Selection field is covered by index. Hazelcast
has three instances, no local cache.

| Operation     | protobuf | pojo    |
|---------------|----------|---------|
| executor      | 204ms    | 227ms   |
| sql predicate | 63ms     | 255ms   |
| sql           | -        | 303ms   |
