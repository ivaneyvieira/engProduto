DROP TABLE IF EXISTS sqldados.iprdAdicionalDev;
CREATE TABLE sqldados.iprdAdicionalDev
(
  invno          int           NOT NULL,
  prdno          varchar(16)   NOT NULL,
  grade          varchar(8)    NOT NULL,
  numero         int           NOT NULL,
  tipoDevolucao  int DEFAULT 0 NOT NULL,
  quantDevolucao int DEFAULT 0 NULL,
  PRIMARY KEY (invno, prdno, grade, tipoDevolucao, numero)
);

SELECT @rowcount := 0;

DROP TEMPORARY TABLE IF EXISTS T_SEQ;
CREATE TEMPORARY TABLE T_SEQ
(
  PRIMARY KEY (invno)
)
SELECT @rowcount := @rowcount + 1 AS seq, invno
FROM
  sqldados.iprdAdicional
WHERE quantDevolucao > 0
GROUP BY invno
ORDER BY invno;

SELECT *
FROM
  T_SEQ;

INSERT INTO sqldados.iprdAdicionalDev(invno, prdno, grade, numero, tipoDevolucao, quantDevolucao)
  SELECT invno, prdno, grade, seq AS numero, tipoDevolucao, quantDevolucao
  FROM
    sqldados.iprdAdicional
      INNER JOIN T_SEQ
                 USING (invno)
  WHERE quantDevolucao > 0;

SELECT *
FROM
  sqldados.iprdAdicionalDev