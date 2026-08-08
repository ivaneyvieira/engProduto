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
  AND N.invse = :nfse
  AND :nfno != '';

DROP TEMPORARY TABLE IF EXISTS T_NF;
CREATE TEMPORARY TABLE IF NOT EXISTS T_NF
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
               USING (prdno, grade);

DROP TEMPORARY TABLE IF EXISTS T_PED;
CREATE TEMPORARY TABLE IF NOT EXISTS T_PED
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno, P.ordno
FROM
  sqldados.eord               AS P
    INNER JOIN sqldados.custp AS C
               ON C.no = P.custno
    INNER JOIN sqldados.store AS S
               ON S.cgc = C.cpf_cgc AND (S.no = P.storeno)
WHERE (P.storeno IN (2, 3, 4, 5, 8))
  AND (P.storeno = :lojaUser OR :lojaUser = 0)
  AND P.ordno = :pedido
  AND P.paymno = 431
  AND :pedido > 0;

DROP TEMPORARY TABLE IF EXISTS T_PED_PRD;
CREATE TEMPORARY TABLE IF NOT EXISTS T_PED_PRD
SELECT I.prdno                            AS prdno,
       I.grade                            AS grade,
       P.name                             AS descricao,
       TRIM(IFNULL(B.barcode, P.barcode)) AS barcode,
       ROUND(I.qtty / 1000)               AS movimentacao
FROM
  sqldados.eoprd               AS I
    INNER JOIN T_PED           AS N
               ON N.storeno = I.storeno AND N.ordno = I.ordno
    INNER JOIN sqldados.prd    AS P
               ON I.prdno = P.no
    LEFT JOIN  sqldados.prdbar AS B
               USING (prdno, grade);

SELECT prdno, grade, descricao, barcode, movimentacao
FROM T_NF
UNION
SELECT prdno, grade, descricao, barcode, movimentacao
FROM T_PED_PRD