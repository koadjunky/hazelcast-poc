# hazelcast-poc

## Hazelcast

Start hazelcast cluster:

```docker compose up```

Scale hazelcast cluster (management cluster works up to 2 nodes without license):

```docker compose up --scale hazelcast=5```

Access management center:

```http://localhost:8080/clusters/dev``` (admin/hazelcast12)

Shut down the cluster

```docker compose down```
