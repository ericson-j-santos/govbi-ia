# Lock distribuído do worker

O lock evita que dois workers processem o mesmo item simultaneamente.

Configuração:

```bash
export GOVBI_LOCK_TIPO=sqlserver
export GOVBI_LOCK_TTL_SEGUNDOS=120
```

Em ambiente local, `memoria` é suficiente. Em produção com múltiplas réplicas, use `sqlserver`.
