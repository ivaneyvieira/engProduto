USE sqldados;

DROP TABLE IF EXISTS nfSaidaArquivoDevolucao;
CREATE TABLE nfSaidaArquivoDevolucao
(
  seq      int AUTO_INCREMENT
    PRIMARY KEY,
  storeno  SMALLINT DEFAULT 0 NOT NULL,
  pdvno    SMALLINT DEFAULT 0 NOT NULL,
  xano     INT      DEFAULT 0 NOT NULL,
  date     int                NOT NULL,
  filename varchar(100)       NOT NULL,
  file     mediumblob         NULL,

  INDEX (storeno, pdvno, xano)
);

/**************************************************/

