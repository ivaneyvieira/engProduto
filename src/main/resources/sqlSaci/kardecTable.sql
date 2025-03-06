DROP TABLE sqldados.produtoKardec;

CREATE TABLE sqldados.produtoKardec
(
  loja       int,
  prdno      varchar(16),
  grade      varchar(8),
  data       date,
  doc        varchar(30),
  tipo       varchar(30),
  vencimento date NULL,
  qtde       Int,
  saldo      Int,
  userLogin  varchar(50),
  PRIMARY KEY (loja, prdno, grade, data, doc, tipo)
);

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN dataUpdate date NULL;

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN kardec int NULL;

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN dataObservacao date NULL;

ALTER TABLE sqldados.prdAdicional
  DROP COLUMN observacao;

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN observacao varchar(100) NULL;