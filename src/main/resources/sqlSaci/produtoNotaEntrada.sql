USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE IF NOT EXISTS T_INV
(
  PRIMARY KEY (invno)
)
SELECT invno
FROM sqldados.inv AS N
WHERE N.storeno = :loja
  AND N.nfname = :nfno
  AND N.invse = :nfse;


SELECT I.prdno                            AS prdno,
       I.grade                            AS grade,
       P.name                             AS descricao,
       TRIM(IFNULL(B.barcode, P.barcode)) AS barcode,
       ROUND(I.qtty / 1000)               AS movimentacao
FROM
  sqldados.iprd                AS I
    INNER JOIN T_INV           AS N
               USING (invno)
    INNER JOIN sqldados.prd    AS P
               ON I.prdno = P.no
    LEFT JOIN  sqldados.prdbar AS B
               USING (prdno, grade)