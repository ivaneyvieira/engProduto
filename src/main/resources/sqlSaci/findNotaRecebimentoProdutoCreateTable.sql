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


DROP TEMPORARY TABLE IF EXISTS T_NOTA_RECEBIMENTO;
CREATE TEMPORARY TABLE T_NOTA_RECEBIMENTO
(
  PRIMARY KEY (invno)
)
SELECT invno, s26
FROM
  sqldados.inv
WHERE s26 > 0
  AND date >= 20240501
  AND storeno IN (1, 2, 3, 4, 5, 6, 7, 8);


UPDATE sqldados.iprd AS I INNER JOIN T_NOTA_RECEBIMENTO AS N USING (invno)
SET I.s26 = N.s26
WHERE I.s26 != N.s26;


ALTER TABLE sqldados.iprdAdicional
  ADD selecionado BOOLEAN DEFAULT FALSE;

select * from sqldados.iprdAdicional;