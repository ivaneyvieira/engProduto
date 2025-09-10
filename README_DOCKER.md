# Execução via Docker

Este projeto já inclui os arquivos para build e execução via Docker.

## Arquivos
- `Dockerfile`: constrói a aplicação (Gradle + JDK 21, Vaadin em modo produção).
- `docker-compose.yml`: sobe o serviço com mapeamento de porta e rede customizada.
- `.dockerignore`: reduz o contexto de build para builds mais rápidos.
- `variables.env`: variáveis para nome da imagem/contêiner, porta e rede.
- `ebean.properties`: configurações de banco (montado no contêiner em `/etc/ebean.properties`).

## Pré‑requisitos
- Docker e Docker Compose instalados.
- Ajustar `ebean.properties` para apontar para bancos acessíveis do contêiner.
- Confirmar que a sub‑rede `172.195.0.0/16` não conflita na sua máquina.

## Como rodar
1. Ajuste variáveis em `variables.env` se necessário:
   - `PORT` define a porta do host (padrão 8036).
2. Build da imagem:
   `docker compose build`
3. Subir o serviço:
   `docker compose up -d`
4. Acessar a aplicação:
   - `http://localhost:${PORT}` (padrão: `http://localhost:8036`)

## Notas
- O Compose carrega `variables.env` automaticamente e calcula IP/`subnet` a partir de `NET_IP`.
- O arquivo `ebean.properties` é montado como volume e não é empacotado na imagem.
- O `Dockerfile` garante a presença do `unzip` durante o build do pacote de distribuição.
