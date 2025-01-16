SET
SQL_MODE = '';

DROP TABLE IF EXISTS T_PRD;
CREATE
TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT S.no AS storeno,
       S.sname AS abrevLoja,
       P.no AS prdno,
       TRIM(P.no) * 1 AS codigo,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       IFNULL(B.grade, '') AS grade,
       TRIM(MID(P.name, 37, 3)) AS unidade,
       IF(tipoGarantia = 2, garantia, 0) AS validade,
       P.mfno AS vendno,
       V.sname AS fornecedorAbrev
FROM sqldados.prd AS P
         LEFT JOIN sqldados.prdbar AS B
                   ON P.no = B.prdno
         LEFT JOIN sqldados.vend AS V
                   ON V.no = P.mfno
         INNER JOIN sqldados.store AS S
                    ON S.no IN (2, 3, 4, 5, 8)
WHERE IF(tipoGarantia = 2, garantia, 0) > 0
GROUP BY S.no, prdno, TRIM(MID(P.name, 1, 37)), grade;

DROP
TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE
TEMPORARY TABLE T_NOTA
SELECT N.storeno AS storeno,
       I.prdno AS prdno,
       I.grade AS grade,
       CAST(N.date AS DATE) AS date,
       DATE_FORMAT(A.vencimento, '%Y%m') * 1 AS mesAno,
       ROUND(SUM(I.qtty / 1000))             AS qtty
FROM sqldados.iprd AS I
    INNER JOIN sqldados.inv AS N
    USING (invno)
    INNER JOIN sqldados.iprdAdicional AS A
ON A.invno = I.invno
    AND A.prdno = I.prdno
    AND A.grade = I.grade
WHERE N.dataSaida >= :dataInicial
  AND N.storeno IN (2
    , 3
    , 4
    , 5
    , 8)
  AND (N.type = 0)
  AND (N.bits & POW(2
    , 4) = 0)
GROUP BY N.storeno, I.prdno, I.grade, N.date, mesAno;

DROP TABLE IF EXISTS T_STK;
CREATE
TEMPORARY TABLE T_STK
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno,
       prdno,
       grade,
       ROUND(SUM(qtty_varejo + stk.qtty_atacado) / 1000) AS estoqueLoja
FROM sqldados.stk
         INNER JOIN T_PRD
                    USING (storeno, prdno, grade)
WHERE storeno IN (2, 3, 4, 5, 8)
GROUP BY storeno, prdno, grade;

DROP TABLE IF EXISTS T_STK_TOTAL;
CREATE
TEMPORARY TABLE T_STK_TOTAL
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM(estoqueLoja) AS estoqueTotal
FROM T_STK
GROUP BY prdno, grade;

DROP TABLE IF EXISTS sqldados.produtoEntrada;
CREATE TABLE IF NOT EXISTS sqldados.produtoEntrada
(
    PRIMARY
    KEY
(
    loja,
    prdno,
    grade,
    date,
    mesano
)
    )
SELECT P.storeno AS loja,
       P.abrevLoja AS lojaAbrev,
       prdno AS prdno,
       codigo AS codigo,
       descricao AS descricao,
       unidade AS unidade,
       validade AS validade,
       vendno AS vendno,
       fornecedorAbrev AS fornecedorAbrev,
       estoqueTotal AS estoqueTotal,
       estoqueLoja AS estoqueLoja,
       P.grade AS grade, date AS date, mesAno AS mesAno, qtty AS qtty
FROM T_NOTA
    INNER JOIN T_PRD AS P
    USING (storeno, prdno, grade)
    LEFT JOIN T_STK
    USING (storeno, prdno, grade)
    LEFT JOIN T_STK_TOTAL AS S
    USING (prdno, grade)