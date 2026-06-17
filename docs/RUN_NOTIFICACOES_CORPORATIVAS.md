# Execução com notificações corporativas

## Teams

```bash
export GOVBI_NOTIFICACAO_HABILITADA=true
export GOVBI_TEAMS_HABILITADO=true
export GOVBI_TEAMS_WEBHOOK_URL='https://...'
```

Use apenas webhooks corporativos aprovados. Não grave o webhook em repositório.

## E-mail SMTP

```bash
export GOVBI_EMAIL_HABILITADO=true
export GOVBI_EMAIL_REMETENTE='govbi@empresa.gov.br'
export SPRING_MAIL_HOST='smtp.empresa.gov.br'
export SPRING_MAIL_PORT=587
export SPRING_MAIL_USERNAME='govbi'
export SPRING_MAIL_PASSWORD='***'
```

O assunto e corpo são montados por template. A mensagem não contém SQL completo nem PII bruta.
