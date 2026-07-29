USE sqldados;

-- auto-generated definition
CREATE TABLE nfAutorizacao
(
  storeno          int                      NOT NULL,
  pdvno            int                      NOT NULL,
  xano             int                      NOT NULL,
  usernoSing       int                      NULL,
  tipoDev          varchar(20)              NULL,
  observacao       varchar(100)             NULL,
  impresso         varchar(1)   DEFAULT 'N' NULL,
  dataInsert       int          DEFAULT 0   NULL,
  autoriza         varchar(1)   DEFAULT 'N' NULL,
  solicitacaoTroca varchar(1)   DEFAULT ''  NULL,
  produtoTroca     varchar(1)   DEFAULT ''  NULL,
  userTroca        int          DEFAULT 0   NULL,
  userSolicitacao  int          DEFAULT 0   NULL,
  motivoTroca      text                     NULL,
  motivoTrocaCod   varchar(100) DEFAULT ''  NULL,
  nfEntRet         int          DEFAULT 0   NULL,
  PRIMARY KEY (storeno, pdvno, xano)
) ENGINE = MyISAM
  CHARSET = latin1;

/****************************************************/

ALTER TABLE nfAutorizacao
  ADD COLUMN invno int(10) DEFAULT 0 AFTER xano;

ALTER TABLE nfAutorizacao
  DROP PRIMARY KEY;

ALTER TABLE nfAutorizacao
  ADD PRIMARY KEY (storeno, pdvno, xano, invno);