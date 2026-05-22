# VitaEquus API

## Profiles e banco

O profile padrao e `dev`. Ele usa MySQL local e aceita overrides via `DB_URL`,
`DB_USERNAME` e `DB_PASSWORD`.

Producao usa o profile `prod` e espera PostgreSQL por variaveis de ambiente.
Com `spring.jpa.hibernate.ddl-auto=validate`, o schema PostgreSQL precisa
existir antes da aplicacao subir.

## Como configurar .env localmente

Copie `.env.example` para `.env`, preencha as variaveis locais e rode a
aplicacao:

```powershell
.\mvnw.cmd spring-boot:run
```

O `.env` e uma conveniencia local. Em hospedagem, como no Render, configure as
mesmas chaves como Environment Variables do servico.

## Variaveis de producao

Configure no Render ou servico equivalente:

```properties
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://host/neondb?sslmode=require&channelBinding=require
DB_USERNAME=neondb_owner
DB_PASSWORD=senha_do_neon
JWT_SECRET=segredo_forte
JWT_ISSUER=vitaequus-api
JWT_AUDIENCE=vitaequus-client
CORS_ALLOWED_ORIGINS=https://seu-front.com
UPLOAD_BASE_DIR=uploads
UPLOAD_ANIMAIS_DIR=animais
UPLOAD_PUBLIC_PATH=/uploads
```

Nao versionar senhas, `.env` locais ou URLs de conexao que contenham segredo.
