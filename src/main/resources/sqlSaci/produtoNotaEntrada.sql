USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE IF NOT EXISTS T_INV
(
  PRIMARY KEY (invno)
)
SELECT invno, storeno
FROM sqldados.inv AS N
WHERE N.storeno = :loja
  AND N.nfname = :nfno
  AND N.invse = :nfse
  AND :nfno != ''
  AND :pedido = 0;

DROP TEMPORARY TABLE IF EXISTS T_NF;
CREATE TEMPORARY TABLE IF NOT EXISTS T_NF
SELECT N.storeno                          AS loja,
       I.prdno                            AS prdno,
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
SELECT P.storeno, P.ordno, P.paymno, IFNULL(S.no, 0) AS lojaCliente
FROM
  sqldados.eord               AS P
    INNER JOIN sqldados.custp AS C
               ON C.no = P.custno
    LEFT JOIN  sqldados.store AS S
               ON S.cgc = C.cpf_cgc AND (S.no = P.storeno)
WHERE (P.storeno IN (2, 3, 4, 5, 8))
  AND (P.storeno = :loja)
  AND P.ordno = :pedido
  AND :pedido > 0
  AND :nfno = '';

DROP TEMPORARY TABLE IF EXISTS T_PED_PRD;
CREATE TEMPORARY TABLE IF NOT EXISTS T_PED_PRD
SELECT I.storeno                          AS loja,
       I.prdno                            AS prdno,
       I.grade                            AS grade,
       P.name                             AS descricao,
       TRIM(IFNULL(B.barcode, P.barcode)) AS barcode,
       ROUND(I.qtty / 1000)               AS movimentacao,
       paymno,
       lojaCliente
FROM
  sqldados.eoprd               AS I
    INNER JOIN T_PED           AS N
               ON N.storeno = I.storeno AND N.ordno = I.ordno
    INNER JOIN sqldados.prd    AS P
               ON I.prdno = P.no
    LEFT JOIN  sqldados.prdbar AS B
               USING (prdno, grade);

SELECT 'N' AS tipo, loja, prdno, grade, descricao, barcode, movimentacao, 0 AS paymno, 0 AS lojaCliente
FROM T_NF
UNION
SELECT 'P' AS tipo, loja, prdno, grade, descricao, barcode, movimentacao, paymno, lojaCliente
FROM T_PED_PRD