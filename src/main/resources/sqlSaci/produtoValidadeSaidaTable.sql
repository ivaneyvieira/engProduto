SET SQL_MODE = '';


DROP TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT S.no                              AS storeno,
       S.sname                           AS abrevLoja,
       P.no                              AS prdno,
       TRIM(P.no) * 1                    AS codigo,
       TRIM(MID(P.name, 1, 37))          AS descricao,
       IFNULL(B.grade, '')               AS grade,
       TRIM(MID(P.name, 37, 3))          AS unidade,
       IF(tipoGarantia = 2, garantia, 0) AS validade,
       P.mfno                            AS vendno,
       V.sname                           AS fornecedorAbrev
FROM
  sqldados.prd                 AS P
    LEFT JOIN  sqldados.prdbar AS B
               ON P.no = B.prdno
    LEFT JOIN  sqldados.vend   AS V
               ON V.no = P.mfno
    INNER JOIN sqldados.store  AS S
               ON S.no IN (2, 3, 4, 5, 8)
WHERE IF(tipoGarantia = 2, garantia, 0) > 0
GROUP BY S.no, prdno, TRIM(MID(P.name, 1, 37)), grade;

DROP TABLE IF EXISTS T_VENDA;
CREATE TEMPORARY TABLE T_VENDA
(
  INDEX (prdno)
)
SELECT N.storeno                 AS lojaOrigem,
       I.storeno                 AS lojaDestino,
       SD.sname                  AS abrevDestino,
       X.prdno                   AS prdno,
       X.grade                   AS grade,
       CAST(N.issuedate AS DATE) AS date,
       ROUND(SUM(X.qtty / 1000)) AS qtty
FROM
  sqldados.nf                  AS N
    INNER JOIN sqldados.xaprd2 AS X
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.inv    AS I
               ON N.invno = I.invno
    LEFT JOIN  sqldados.store  AS SD
               ON SD.no = I.storeno
WHERE N.issuedate >= :dataInicial
  AND N.storeno IN (2, 3, 4, 5, 8)
  AND N.status <> 1
GROUP BY lojaOrigem, lojaDestino, prdno, grade, date;

DROP TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno, prdno, grade, ROUND(SUM(qtty_varejo + stk.qtty_atacado) / 1000) AS estoqueSaci
FROM
  sqldados.stk
    INNER JOIN T_PRD
               USING (storeno, prdno, grade)
WHERE storeno IN (2, 3, 4, 5, 8)
GROUP BY storeno, prdno, grade;

DROP TABLE IF EXISTS T_STK_TOTAL;
CREATE TEMPORARY TABLE T_STK_TOTAL
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM(estoqueSaci) AS estoqueTotal
FROM
  T_STK
GROUP BY prdno, grade;

DROP TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT lojaOrigem               AS lojaOrigem,
       abrevLoja                AS abrevLoja,
       IFNULL(lojaDestino, 0)   AS lojaDestino,
       IFNULL(abrevDestino, '') AS abrevDestino,
       V.grade                  AS grade,
       date                     AS date,
       qtty                     AS qtty,
       V.prdno                  AS prdno,
       codigo                   AS codigo,
       descricao                AS descricao,
       unidade                  AS unidade,
       validade                 AS validade,
       vendno                   AS vendno,
       fornecedorAbrev          AS fornecedorAbrev,
       estoqueTotal             AS estoqueTotal
FROM
  T_VENDA                  AS V
    INNER JOIN T_PRD       AS P
               ON V.lojaOrigem = P.storeno AND V.prdno = P.prdno AND V.grade = P.grade
    LEFT JOIN  T_STK_TOTAL AS S
               ON V.prdno = S.prdno AND V.grade = S.grade;


DROP TABLE IF EXISTS sqldados.produtoSaida;
CREATE TABLE sqldados.produtoSaida
(
  PRIMARY KEY (lojaOrigem, lojaDestino, prdno, grade, date)
)
SELECT lojaOrigem,
       abrevLoja,
       lojaDestino,
       abrevDestino,
       grade,
       date,
       qtty,
       prdno,
       codigo,
       descricao,
       unidade,
       validade,
       vendno,
       fornecedorAbrev,
       estoqueTotal
FROM
  T_QUERY

