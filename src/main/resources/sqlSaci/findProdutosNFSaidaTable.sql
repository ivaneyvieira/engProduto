USE sqldados;

SET SQL_MODE = '';

-- auto-generated definition
CREATE TABLE xaprd2Devolucao
(
  storeno  smallint DEFAULT 0  NOT NULL,
  pdvno    smallint DEFAULT 0  NOT NULL,
  xano     int      DEFAULT 0  NOT NULL,
  prdno    char(16) DEFAULT '' NOT NULL,
  grade    char(8)  DEFAULT '' NOT NULL,
  quantDev    int                 NULL,
  PRIMARY KEY (storeno, pdvno, xano, prdno, grade)
)
  ENGINE = MyISAM
  CHARSET = latin1;
