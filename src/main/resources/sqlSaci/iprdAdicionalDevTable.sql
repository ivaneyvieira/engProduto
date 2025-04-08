DROP TABLE IF EXISTS sqldados.iprdAdicionalDev;
CREATE TABLE sqldados.iprdAdicionalDev
(
  invno          int           NOT NULL,
  prdno          varchar(16)   NOT NULL,
  grade          varchar(8)    NOT NULL,
  numero         int           NOT NULL,
  tipoDevolucao  int DEFAULT 0 NULL,
  quantDevolucao int DEFAULT 0 NULL,
  PRIMARY KEY (invno, prdno, grade, numero)
);

INSERT INTO sqldados.iprdAdicionalDev(invno, prdno, grade, numero, tipoDevolucao, quantDevolucao)
SELECT invno, prdno, grade, 1 AS numero, tipoDevolucao, quantDevolucao
FROM
  sqldados.iprdAdicional
WHERE quantDevolucao > 0;

SELECT *
FROM
  sqldados.iprdAdicionalDev