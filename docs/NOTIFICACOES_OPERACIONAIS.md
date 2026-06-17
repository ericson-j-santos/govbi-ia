# Notificações operacionais

A v0.8.0 introduz `NotificacaoOperacionalPort`. O modo padrão registra a notificação e emite log estruturado. Adapters reais de Teams/e-mail podem ser conectados sem alterar os casos de uso.

## Tipos iniciais

- `RESULTADO_DISPONIVEL`
- `FALHA_PROCESSAMENTO`
- `APROVACAO_EXPIRADA`

## Configuração

```bash
GOVBI_NOTIFICACAO_HABILITADA=true
GOVBI_NOTIFICACAO_CANAIS=LOG,TEAMS,EMAIL
GOVBI_TEAMS_WEBHOOK_URL=https://...
GOVBI_EMAIL_REMETENTE=govbi@empresa.local
```
