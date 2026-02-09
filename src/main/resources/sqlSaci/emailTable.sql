DROP TABLE IF EXISTS sqldados.emailDevolucao;
CREATE TABLE sqldados.emailDevolucao
(
  id          INTEGER PRIMARY KEY AUTO_INCREMENT,
  chave       VARCHAR(30)   NOT NULL DEFAULT '',
  dataEmail   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fromEmail   varchar(60)   NOT NULL DEFAULT '',
  toEmail     varchar(1000) NOT NULL DEFAULT '',
  ccEmail     varchar(1000) NOT NULL DEFAULT '',
  bccEmail    varchar(1000) NOT NULL DEFAULT '',
  subject     varchar(255)  NOT NULL DEFAULT '',
  enviado     boolean       NOT NULL DEFAULT FALSE,
  htmlContent TEXT,
  INDEX (chave)
);

DROP TABLE IF EXISTS sqldados.anexoEmail;
CREATE TABLE sqldados.anexoEmail
(
  id          INTEGER PRIMARY KEY AUTO_INCREMENT,
  idEmail     INTEGER REFERENCES sqldados.emailDevolucao (id),
  nomeArquivo VARCHAR(255) NOT NULL DEFAULT '',
  conteudo    BLOB
);
