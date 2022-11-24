# hazelcast-poc

## Docker Compose

Start docker compose:

```docker compose up```

Shut docker compose:

```docker compose down```

## Hazelcast

Scale hazelcast cluster (management cluster works up to 2 nodes without license):

```docker compose up --scale hazelcast=5```

Access management center:

```http://localhost:8080/clusters/dev``` (admin/ChangeM3)

## InfluxDB

InfluxDB console is available at:

```http://localhost:8086/``` (admin/adminuser)
