# Plano de Testes — Incremento 9

## Unitários
- Template de notificação renderiza variáveis.
- Lock em memória impede aquisição concorrente.
- DLQ registra falha definitiva.
- Download bloqueia perfil não autorizado.

## Integração
- Worker processa item com lock.
- Falha após tentativas cria DLQ.
- Notificação Teams usa payload mínimo.
- SMTP usa remetente configurado.

## Segurança
- Tokens, senhas e webhook não aparecem em logs.
- Download não libera resultado expirado.
- DLQ não armazena stack trace bruto com segredos.
