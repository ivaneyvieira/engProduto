USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno)
)
SELECT I.invno     AS invno,
       I.date      AS data,
       I.storeno   AS codLoja,
       S.otherName AS loja
FROM sqldados.inv AS I
       LEFT JOIN sqldados.store AS S
                 ON S.no = I.storeno
WHERE (I.date = :data)
  AND (I.storeno = :loja)
  AND I.bits & POW(2, 4) = 0
  AND I.account = '2.01.25';

SELECT CAST(data AS DATE)        AS data,
       I.codLoja                 AS codLoja,
       I.loja                    AS loja,
       X.prdno                   AS prdno,
       TRIM(X.prdno)             AS codigo,
       TRIM(MID(P.name, 1, 37))  AS descricao,
       X.grade                   AS grade,
       SUM(ROUND(X.qtty / 1000)) AS quantidade
FROM T_NOTA AS I
       INNER JOIN sqldados.iprd AS X
                  ON I.invno = X.invno
       LEFT JOIN sqldados.prd AS P
                 ON P.no = X.prdno
GROUP BY I.codLoja, X.prdno, X.grade
ORDER BY descricao, grade, codigo


