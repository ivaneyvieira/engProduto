USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno, prdno, grade, MAX(dataVenda * 1) AS dataVenda
FROM
  sqldados.qtd_vencimento
WHERE storeno IN (2, 3, 4, 5, 8)
  AND dataVenda IS NOT NULL
  AND ((storeno = :storeno AND prdno = :prdno AND grade = :grade) OR (:storeno = 0))
GROUP BY storeno, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_VENDAS;
CREATE TEMPORARY TABLE T_VENDAS
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT P.storeno AS storeno, P.prdno AS prdno, P.grade AS grade, SUM(ROUND(X.qtty / 1000)) AS vendas
FROM
  T_PRD                       AS P
    LEFT JOIN sqldados.xaprd2 AS X
              USING (storeno, prdno, grade)
    LEFT JOIN sqldados.nf     AS N
              USING (storeno, pdvno, xano)
WHERE (N.issuedate >= P.dataVenda)
  AND N.status <> 1
GROUP BY P.storeno, P.prdno, P.grade;

DROP TEMPORARY TABLE IF EXISTS T_STKMOV;
CREATE TEMPORARY TABLE T_STKMOV
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT P.storeno AS storeno, P.prdno AS prdno, P.grade AS grade, SUM(ROUND(-S.qtty / 1000)) AS vendas
FROM
  T_PRD                       AS P
    LEFT JOIN sqldados.stkmov AS S
              USING (storeno, prdno, grade)
WHERE (S.date >= P.dataVenda)
  AND (S.qtty < 0)
GROUP BY P.storeno, P.prdno, P.grade;

DROP TEMPORARY TABLE IF EXISTS T_SAIDAS;
CREATE TEMPORARY TABLE T_SAIDAS
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno, prdno, grade, SUM(vendas) AS vendas
FROM
  ( SELECT storeno, prdno, grade, vendas
    FROM T_VENDAS
    UNION
    DISTINCT
    SELECT storeno, prdno, grade, vendas
    FROM T_STKMOV ) AS S
GROUP BY storeno, prdno, grade;

UPDATE sqldados.qtd_vencimento AS Q
  INNER JOIN T_SAIDAS AS S
  USING (storeno, prdno, grade)
SET Q.vendas = IFNULL(S.vendas, 0)
WHERE Q.storeno IN (2, 3, 4, 5, 8)
  AND Q.dataVenda IS NOT NULL