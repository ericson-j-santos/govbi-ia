# Matriz de configuração por ambiente — v1.0.1

| Variável | Local | Homologação | Produção |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `local` | `hom` | `prod` |
| `GOVBI_RELEASE_MODO` | `release-candidate` | `homologacao` | `producao` |
| `GOVBI_OIDC_HABILITADO` | `false` | `true` | `true` |
| `GOVBI_PERSISTENCIA_OPERACIONAL_TIPO` | `memoria` | `sqlserver` | `sqlserver` |
| `GOVBI_DADOS_EXECUTOR` | `mock` | `sqlserver` | `sqlserver` |
| `GOVBI_DADOS_PERMITIR_EXECUCAO_REAL` | `false` | `false` | `true` |
| `GOVBI_LOCK_TIPO` | `memoria` | `sqlserver` | `sqlserver` |
| `GOVBI_WORKER_HABILITADO` | `false` | `false` | `true` |
| `GOVBI_DOWNLOAD_HABILITADO` | `true` | `true` | `true` |
| `GOVBI_NOTIFICACAO_CANAIS_ATIVOS` | `LOG` | `LOG,TEAMS` | `LOG,TEAMS,EMAIL` |

## Observação
A transição de homologação para produção exige mudança explícita de execução real, worker e canais corporativos.
