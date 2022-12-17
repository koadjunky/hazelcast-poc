# hazelcast-poc

## Docker Compose

Start docker compose:

```docker compose up```

Shut docker compose:

```docker compose down```

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
