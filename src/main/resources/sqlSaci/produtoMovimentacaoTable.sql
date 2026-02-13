CREATE TABLE sqldados.produtoMovimentacao
(
  numero        int                    NOT NULL,
  numloja       int                    NOT NULL,
  data          date                   NOT NULL,
  hora          time                   NOT NULL,
  usuario       varchar(50)            NOT NULL,
  acertoSimples tinyint(1)  DEFAULT 0  NULL,
  prdno         varchar(16)            NOT NULL,
  grade         varchar(20)            NOT NULL,
  estoqueSis    int                    NULL,
  estoqueLoja   int                    NULL,
  estoqueCD     int                    NULL,
  diferenca     int                    NULL,
  gravado       tinyint(1)  DEFAULT 0  NULL,
  gravadoLogin  int         DEFAULT 0  NULL,
  processado    tinyint(1)  DEFAULT 0  NULL,
  transacao     varchar(20) DEFAULT '' NULL,
  login         varchar(20) DEFAULT '' NULL,
  PRIMARY KEY (numloja, numero, prdno, grade)
)
  ENGINE = MyISAM
  CHARSET = latin1;

