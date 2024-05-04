USE sqldados;

SET SQL_MODE = '';

CREATE TABLE sqldados.iprdAdicional
(
  invno            INT         NOT NULL,
  prdno            VARCHAR(16) NOT NULL,
  grade            VARCHAR(8)  NOT NULL,
  marcaRecebimento INT,
  PRIMARY KEY (invno, prdno, grade)
);

alter table sqldados.iprdAdicional
  add login VARCHAR(20) DEFAULT '';