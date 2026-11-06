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
) ENGINE = MyISAM
  CHARSET = latin1;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN transacao;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN processado;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN acertoSimples;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN estoqueCD;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN estoqueLoja;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN estoqueSis;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN diferenca;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN movimentacao int DEFAULT 0 NULL;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN estoque int NULL DEFAULT NULL;

/********************************************************************************/

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN usuario;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN gravado;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN login;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN noLogin int NULL;

UPDATE sqldados.produtoMovimentacao
SET noLogin = 1
WHERE noLogin IS NULL;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN noEntregue int NULL;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN noRecebido int NULL;

ALTER TABLE sqldados.produtoMovimentacao RENAME COLUMN gravadoLogin TO noGravado;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN noRota int NULL DEFAULT 0;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN dataEntrege int NULL DEFAULT 0;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN horaEntrege int NULL DEFAULT 0;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN dataRecebido int NULL DEFAULT 0;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN horaRecebido int NULL DEFAULT 0;

/********************************************************************************/

SELECT *
FROM sqldados.produtoMovimentacao;

SELECT *
FROM sqldados.users
WHERE no = 1;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN observacao VARCHAR(200) NULL DEFAULT NULL;

ALTER TABLE sqldados.produtoMovimentacao
  DROP COLUMN noConfrimacao;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN noConfirmacao int NULL DEFAULT 0 AFTER noGravado;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN impresso int NULL DEFAULT 0 AFTER noConfirmacao;

ALTER TABLE sqldados.produtoMovimentacao
  ADD COLUMN localizacao varchar(20) NULL DEFAULT '' AFTER noRota;

DROP TEMPORARY TABLE IF EXISTS sqldados.T_LOC_APP;
CREATE TEMPORARY TABLE sqldados.T_LOC_APP
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT P.storeno, P.prdno, P.grade, P.localizacao AS locApp
FROM sqldados.prdAdicional AS P
GROUP BY P.storeno, P.prdno, P.grade;

UPDATE sqldados.produtoMovimentacao AS P INNER JOIN sqldados.T_LOC_APP AS A ON A.storeno = P.numloja AND A.prdno = P.prdno AND A.grade = P.grade
SET P.localizacao = A.locApp
WHERE P.localizacao != A.locApp;

CREATE INDEX i1 on sqldados.prdAdicional(localizacao);