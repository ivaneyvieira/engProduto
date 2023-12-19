USE sqldados;

USE sqldados;

SET sql_mode = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno)
)
SELECT I.invno                        AS invno,
       CAST(I.date AS DATE)           AS data,
       I.storeno                      AS codLoja,
       CONCAT(I.nfname, '/', I.invse) AS nota,
       S.otherName                    AS loja,
       I.remarks                      AS observacao
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
       SUM(ROUND(X.qtty / 1000)) AS quantidade,
       observacao                AS observacao,
       I.invno                   AS ni,
       I.nota                    AS nota
FROM T_NOTA AS I
       INNER JOIN sqldados.iprd AS X
                  ON I.invno = X.invno
       LEFT JOIN sqldados.prd AS P
                 ON P.no = X.prdno
WHERE (@PESQUISA = '' OR
       I.codLoja = @PESQUISANUM OR
       TRIM(prdno) = @PESQUISANUM OR
       TRIM(MID(P.name, 1, 37)) LIKE @PESQUISALIKE OR
       X.grade LIKE @PESQUISAS OR
       I.observacao LIKE @PESQUISALIKE OR
       I.nota LIKE @PESQUISASTART OR
       I.invno = @PESQUISANUM)
GROUP BY I.codLoja, X.prdno, X.grade, I.observacao
ORDER BY descricao, grade, codigo


