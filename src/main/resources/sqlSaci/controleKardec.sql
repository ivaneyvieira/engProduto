/*
 '          109798'
 */

USE sqldados;

SET SQL_MODE = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno, prdno, grade)
)
SELECT I.invno,
       I.prdno,
       I.grade,
       N.storeno,
       IFNULL(A.login, '')                                         AS login,
       DATE(N.date)                                                AS data,
       TRIM(LEADING '0' FROM TRIM(CONCAT(N.nfname, '/', N.invse))) AS doc,
       N.remarks                                                   AS observacao,
       IF(A.vencimento = 0, NULL, A.vencimento)                    AS vencimento,
       ROUND(SUM(I.qtty / 1000))                                   AS qtde,
       IFNULL(A.marcaRecebimento, 0)                               AS marca
FROM
  sqldados.iprd                      I
    JOIN      sqldados.inv           N
              USING (invno)
    LEFT JOIN sqldados.iprdAdicional A
              ON A.invno = I.invno AND A.prdno = I.prdno AND A.grade = I.grade
WHERE (N.bits & POW(2, 4) = 0)
  AND (N.date >= :dataInicial OR :dataInicial = 0)
  AND (N.date <= :dataFinal OR :dataFinal = 0)
  AND N.storeno IN (1, 2, 3, 4, 5, 8)
  AND (N.storeno = :loja OR :loja = 0)
  AND (
  N.account IN ('2.01.20', '2.01.21', '4.01.01.04.02', '4.01.01.06.04', '6.03.01.01.01', '6.03.01.01.02')
    OR N.account IN ('2.01.25')
    OR N.type = 1
    OR (N.cfo = 1949 AND N.remarks LIKE '%RECLASS%')
  )
  AND N.cfo NOT IN (1556, 2556, 1933, 2933)
GROUP BY I.invno, I.prdno, I.grade;

DROP TEMPORARY TABLE IF EXISTS T_KARDEC;
CREATE TEMPORARY TABLE `T_KARDEC`
(
  `loja`       int,
  `prdno`      char(16),
  `grade`      char(8),
  `data`       date,
  `doc`        varchar(60),
  `nfEnt`      varchar(30),
  `tipo`       varchar(50),
  `observacao` char(100),
  `vencimento` date,
  `qtde`       int,
  `saldo`      int,
  `userLogin`  varchar(20)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

INSERT INTO T_KARDEC(loja, prdno, grade, data, doc, nfEnt, tipo, observacao, vencimento, qtde, saldo, userLogin)
SELECT storeno       AS loja,
       prdno         AS prdno,
       grade         AS grade,
       data          AS data,
       doc           AS doc,
       ''            AS nfEnt,
       'RECEBIMENTO' AS tipo,
       observacao    AS observacao,
       vencimento    AS vencimento,
       qtde          AS qtde,
       0             AS saldo,
       login         AS userLogin
FROM
  T_NOTA
WHERE (marca = 1)
  AND (prdno = :prdno OR :prdno = '')
  AND (grade = :grade OR :grade = '');

SELECT *
FROM
  T_KARDEC;
