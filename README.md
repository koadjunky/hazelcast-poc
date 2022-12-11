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
