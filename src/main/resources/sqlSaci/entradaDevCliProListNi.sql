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

DROP TEMPORARY TABLE IF EXISTS T_RESULT;
CREATE TEMPORARY TABLE T_RESULT
SELECT CAST(data AS DATE)                                                  AS data,
       I.codLoja                                                           AS codLoja,
       I.loja                                                              AS loja,
       X.prdno                                                             AS prdno,
       U.name                                                              AS userName,
       U.login                                                             AS userLogin,
       TRIM(X.prdno)                                                       AS codigo,
       TRIM(MID(P.name, 1, 37))                                            AS descricao,
       X.grade                                                             AS grade,
       SUM(ROUND(X.qtty / 1000))                                           AS quantidade,
       observacao                                                          AS observacao,
       TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(observacao, ')', 2), ')', -1)) AS tipo,
       SUBSTRING_INDEX(X.c10, '|', 1)                                      AS tipoPrd,
       IF(X.c10 LIKE '%|%',
          SUBSTRING_INDEX(SUBSTRING_INDEX(X.c10, '|', 2), '|', -1),
          '0') * 1                                                         AS tipoQtd,
       I.invno                                                             AS ni,
       I.nota                                                              AS nota,
       I.valor                                                             AS valor
FROM
  T_NOTA                      AS I
    INNER JOIN sqldados.iprd  AS X
               ON I.invno = X.invno
    LEFT JOIN  sqldados.prd   AS P
               ON P.no = X.prdno
    LEFT JOIN  sqldados.users AS U
               ON U.no = I.userno
GROUP BY I.codLoja, X.prdno, X.grade, I.observacao
ORDER BY descricao, grade, codigo;

SELECT data,
       codLoja,
       loja,
       prdno,
       userName,
       userLogin,
       codigo,
       descricao,
       grade,
       quantidade,
       observacao,
       tipo,
       IF(tipo REGEXP '^TRO.* M.*' OR
          tipo REGEXP '^EST.* M.*' OR
          tipo REGEXP '^REE.* M.*', tipoPrd, tipo)     AS tipoPrd,
       tipoQtd,
       IF(IFNULL(tipoQtd, 0) = 0, quantidade, tipoQtd) AS tipoQtdEfetiva,
       ni,
       nota,
       valor
FROM
  T_RESULT


