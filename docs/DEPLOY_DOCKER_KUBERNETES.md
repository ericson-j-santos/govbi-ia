# Deploy Docker/Kubernetes

## Docker

```bash
docker build -t govbi-ia:0.6.0 -f deploy/docker/Dockerfile .
docker compose -f deploy/docker/docker-compose.yml up
```

## Kubernetes

```bash
kubectl apply -f deploy/k8s/configmap.yml
kubectl apply -f deploy/k8s/secret.example.yml
kubectl apply -f deploy/k8s/deployment.yml
kubectl apply -f deploy/k8s/service.yml
```

Os manifests usam execução mock por padrão. Para produção, configure OIDC, executor real, secrets e TLS corporativo.
