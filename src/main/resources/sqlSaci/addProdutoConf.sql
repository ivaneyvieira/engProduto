USE sqldados;

DROP TABLE IF EXISTS T;

CREATE TEMPORARY TABLE T
SELECT P.no                         AS prdno,
       IFNULL(B.grade, '')          AS grade,
       IFNULL(B.barcode, P.barcode) AS barcode
FROM sqldados.prd           AS P
  LEFT JOIN sqldados.prdbar AS B
	      ON B.prdno = P.no
WHERE B.barcode = LPAD(:barcode, 16, ' ')
   OR (P.barcode = LPAD(:barcode, 16, ' '))
GROUP BY prdno, IFNULL(B.grade, '');

REPLACE INTO sqldados.iprdConferencia(prdno, grade, qtty, marca, nfekey)
SELECT prdno,
       grade,
       :quant * 1000 AS qtty,
       0             AS marca,
       :nfekey       AS nfekey
FROM T T2