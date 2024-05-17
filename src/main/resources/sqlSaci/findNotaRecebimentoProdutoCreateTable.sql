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

ALTER TABLE sqldados.iprdAdicional
  ADD login VARCHAR(20) DEFAULT '';

ALTER TABLE sqldados.iprdAdicional
  ADD validade INT DEFAULT 0;

ALTER TABLE sqldados.iprdAdicional
  ADD vencimento DATE NULL;