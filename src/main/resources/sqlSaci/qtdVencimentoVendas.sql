USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno, prdno, grade, MAX(dataVenda * 1) AS dataVenda
FROM sqldados.qtd_vencimento
WHERE storeno IN (2, 3, 4, 5, 8)
  AND dataVenda IS NOT NULL
GROUP BY storeno, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_VENDAS;
CREATE TEMPORARY TABLE T_VENDAS
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT P.storeno AS storeno, P.prdno AS prdno, P.grade AS grade, SUM(ROUND(X.qtty / 1000)) AS vendas
FROM T_PRD AS P
       LEFT JOIN sqldados.xaprd2 AS X
                 USING (storeno, prdno, grade)
       LEFT JOIN sqldados.nf AS N
                 USING (storeno, pdvno, xano)
WHERE (N.issuedate >= P.dataVenda)
  AND N.tipo IN (0, 4)
  AND N.status <> 1
GROUP BY P.storeno, P.prdno, P.grade;

UPDATE sqldados.qtd_vencimento AS Q
  INNER JOIN T_VENDAS AS V
  ON V.storeno = Q.storeno
    AND V.prdno = Q.prdno
    AND V.grade = Q.grade
SET Q.vendas = V.vendas
WHERE Q.storeno IN (2, 3, 4, 5, 8)
  AND Q.dataVenda IS NOT NULL;

