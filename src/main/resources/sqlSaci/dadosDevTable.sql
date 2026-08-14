USE sqldados;

SET sql_mode = '';

DROP TABLE IF EXISTS dadosDev;
CREATE TABLE dadosDev
(
  invno           int        DEFAULT 0  NOT NULL,
  userSolicitacao int        DEFAULT 0  NULL,
  userTroca       int        DEFAULT 0  NULL,
  produtoTroca    varchar(1) DEFAULT '' NULL,
  tipoDev         varchar(20)           NULL,
  nfEntRet        int        DEFAULT 0  NULL,
  PRIMARY KEY (invno)
);

DROP TABLE IF EXISTS dadosDevProduto;
CREATE TABLE dadosDevProduto
(
  invno        int        DEFAULT 0  NOT NULL,
  prdno        varchar(16)           NOT NULL,
  grade        varchar(8)            NOT NULL,
  produtoTroca varchar(1) DEFAULT '' NOT NULL,
  quantidade   int        DEFAULT 0  NULL,
  PRIMARY KEY (invno, prdno, grade, produtoTroca)
);
