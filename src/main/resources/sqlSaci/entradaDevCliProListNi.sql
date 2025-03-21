USE sqldados;

USE sqldados;

SET sql_mode = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno)
)
SELECT I.invno                                            AS invno,
       IF(I.usernoFirst = 0, I.usernoLast, I.usernoFirst) AS userno,
       CAST(I.date AS DATE)                               AS data,
       I.storeno                                          AS codLoja,
       CONCAT(I.nfname, '/', I.invse)                     AS nota,
       S.otherName                                        AS loja,
       I.remarks                                          AS observacao,
       ROUND(I.grossamt / 100, 2)                         AS valor
FROM
  sqldados.inv               AS I
    LEFT JOIN sqldados.store AS S
              ON S.no = I.storeno
WHERE (I.invno IN (:listNi))
  AND (I.bits & POW(2, 4) = 0)
  AND I.account = '2.01.25';

SELECT CAST(data AS DATE)        AS data,
       I.codLoja                 AS codLoja,
       I.loja                    AS loja,
       X.prdno                   AS prdno,
       U.name                    AS userName,
       U.login                   AS userLogin,
       TRIM(X.prdno)             AS codigo,
       TRIM(MID(P.name, 1, 37))  AS descricao,
       X.grade                   AS grade,
       SUM(ROUND(X.qtty / 1000)) AS quantidade,
       observacao                AS observacao,
       I.invno                   AS ni,
       I.nota                    AS nota,
       I.valor                   AS valor
FROM
  T_NOTA                      AS I
    INNER JOIN sqldados.iprd  AS X
               ON I.invno = X.invno
    LEFT JOIN  sqldados.prd   AS P
               ON P.no = X.prdno
    LEFT JOIN  sqldados.users AS U
               ON U.no = I.userno
GROUP BY I.codLoja, X.prdno, X.grade, I.observacao
ORDER BY descricao, grade, codigo


