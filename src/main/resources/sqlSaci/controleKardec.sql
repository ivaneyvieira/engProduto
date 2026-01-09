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

/****************************************************************************************************/

DROP TEMPORARY TABLE IF EXISTS T_TIPO;
CREATE TEMPORARY TABLE T_TIPO
(
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno AS storeno, ordno AS ordno, SUM((E.bits & 2) > 0) AS tipoR, SUM((E.bits & 2) = 0) AS tipoE
FROM
  sqldados.eoprdf AS E
WHERE (storeno IN (2, 3, 4, 5, 8))
  AND (`date` >= :dataInicial)
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_E;
CREATE TEMPORARY TABLE T_E
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
       P.eordno                                  AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       P.date                                    AS data,
       P.s3                                      AS userno,
       U.login                                   AS usuario
FROM
  sqlpdv.pxa                 AS P
    LEFT JOIN sqldados.users AS U
              ON U.no = P.s3
WHERE P.cfo IN (5117, 6117)
  AND P.storeno IN (2, 3, 4, 5, 8)
  AND P.date >= :dataInicial
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_V;
CREATE TEMPORARY TABLE T_V
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
       P.pdvno,
       P.xano,
       P.eordno                                  AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       nfno,
       nfse
FROM
  sqlpdv.pxa AS P
WHERE P.cfo IN (5922, 6922)
  AND storeno IN (2, 3, 4, 5, 8)
  AND nfse = '1'
  AND `date` >= :dataInicial
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_ENTREGA;
CREATE TEMPORARY TABLE T_ENTREGA
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT V.storeno,
       V.pdvno,
       V.xano,
       V.numero       AS notaVenda,
       MAX(E.numero)  AS notaEntrega,
       MAX(E.usuario) AS usuario,
       MAX(E.data)    AS dataEntrega
FROM
  T_V             AS V
    LEFT JOIN T_E AS E
              USING (storeno, ordno)
GROUP BY V.storeno, V.pdvno, V.xano;

DROP TEMPORARY TABLE IF EXISTS T_CARGA;
CREATE TEMPORARY TABLE T_CARGA
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT storeno, pdvno, xano
FROM
  sqldados.nfrprd
WHERE (storenoStk = :loja OR :loja = 0)
  AND storeno != storenoStk
  AND `date` > :dataInicial
  AND optionEntrega % 10 = 4
  AND nfse != 3
GROUP BY storeno, pdvno, xano;

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
(
  PRIMARY KEY (loja, pdvno, xano)
)
SELECT N.storeno                                                   AS loja,
       N.s14 != 0                                                  AS separado,
       N.pdvno                                                     AS pdvno,
       N.xano                                                      AS xano,
       N.nfno                                                      AS numero,
       N.nfse                                                      AS serie,
       N.eordno                                                    AS pedido,
       N.custno                                                    AS cliente,
       ''                                                          AS nomeCliente,
       N.grossamt / 100                                            AS valorNota,
       CAST(N.issuedate AS DATE)                                   AS data,
       SEC_TO_TIME(0)                                              AS hora,
       N.empno                                                     AS vendedor,
       ''                                                          AS nomeVendedor,
       ''                                                          AS nomeCompletoVendedor,
       ''                                                          AS locais,
       MAX(X.c5)                                                   AS usuarioExp,
       MAX(X.c4)                                                   AS usuarioCD,
       SUM((X.qtty / 1000) * X.preco)                              AS totalProdutos,
       2                                                           AS marca,
       IF(N.status <> 1, 'N', 'S')                                 AS cancelada,
       CASE
         WHEN N.remarks LIKE '%RECLASSIFI%' THEN 'RECLASS'
         WHEN N.nfse = 7                    THEN 'ENTREGA_WEB'
         WHEN N.tipo = 0 AND N.nfse >= 10   THEN 'NFCE'
         WHEN N.tipo = 0 AND N.nfse < 10    THEN 'NFE'
         WHEN N.tipo = 1                    THEN 'TRANSFERENCIA'
         WHEN N.tipo = 2                    THEN 'DEVOLUCAO'
         WHEN N.tipo = 3                    THEN IF(N.storeno != :loja AND :loja != 0 AND N.nfse = 3, '', 'SIMP_REME')
         WHEN N.tipo = 4                    THEN IF(IFNULL(T.tipoE, 0) = 0 AND IFNULL(T.tipoR, 0) > 0,
                                                    IF(N.storeno != :loja AND :loja != 0 AND N.nfse = 3, '', 'SIMP_REME'),
                                                    'ENTRE_FUT')
         WHEN N.tipo = 5                    THEN 'RET_DEMON'
         WHEN N.tipo = 6                    THEN 'VENDA_USA'
         WHEN N.tipo = 7                    THEN 'OUTROS'
         WHEN N.tipo = 8                    THEN 'NF_CF'
         WHEN N.tipo = 9                    THEN 'PERD/CONSER'
         WHEN N.tipo = 10                   THEN 'REPOSICAO'
         WHEN N.tipo = 11                   THEN 'RESSARCI'
         WHEN N.tipo = 12                   THEN 'COMODATO'
         WHEN N.tipo = 13                   THEN 'NF_EMPRESA'
         WHEN N.tipo = 14                   THEN 'BONIFICA'
         WHEN N.tipo = 15                   THEN 'NFE'
                                            ELSE ''
       END                                                         AS tipoNotaSaida,
       IFNULL(ENT.notaEntrega, '')                                 AS notaEntrega,
       ENT.usuario                                                 AS usuarioEntrega,
       CAST(ENT.dataEntrega AS DATE)                               AS dataEntrega,
       CASE
         WHEN IFNULL(T.tipoE, 0) > 0 AND IFNULL(T.tipoR, 0) = 0 THEN 'Entrega'
         WHEN IFNULL(T.tipoE, 0) = 0 AND IFNULL(T.tipoR, 0) > 0 THEN 'Retira'
         WHEN IFNULL(T.tipoE, 0) > 0 AND IFNULL(T.tipoR, 0) > 0 THEN 'Misto'
                                                                ELSE ''
       END                                                         AS tipo,
       (IFNULL(CG.storeno, :loja) != :loja) OR (N.storeno = :loja) AS retiraFutura,
       CAST(IF(N.l16 = 0, NULL, N.l16) AS DATE)                    AS entrega,
       MAX(EC.no)                                                  AS usernoSingCD,
       GROUP_CONCAT(DISTINCT IF(EC.login = '', '', EC.login))      AS usuarioSingCD,
       N.print_remarks                                             AS observacaoPrint,
       N.remarks                                                   AS observacao
FROM
  sqldados.nf                       AS N
    LEFT JOIN  sqldados.nfUserPrint AS PT
               USING (storeno, pdvno, xano)
    LEFT JOIN  T_CARGA              AS CG
               USING (storeno, pdvno, xano)
    LEFT JOIN  T_ENTREGA            AS ENT
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.xaprd2      AS X
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.users       AS EC
               ON EC.no = X.s4
    LEFT JOIN  T_TIPO               AS T
               ON N.storeno = T.storeno AND N.eordno = T.ordno
WHERE (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND (N.issuedate >= :dataInicial)
  AND (X.date >= :dataInicial OR :dataInicial = 0)
  AND (X.date <= :dataFinal OR :dataFinal = 0)
  AND (X.date >= :dataInicial)
  AND (X.prdno = :prdno OR :prdno = '')
  AND (X.grade = :grade OR :grade = '')
  AND (X.storeno IN (2, 3, 4, 5, 8))
  AND ((:loja = 0 OR (N.storeno != :loja AND IFNULL(tipoR, 0) = 0 AND N.tipo NOT IN (0, 1)) OR
        (N.storeno = :loja OR (IFNULL(CG.storeno, 0) != :loja AND IFNULL(CG.storeno, 0) != 0))))
  AND ((2 IN (0, 999) AND ((N.tipo = 4 AND IFNULL(T.tipoE, 0) > 0) -- Retira Futura
  OR (N.tipo = 3 AND IFNULL(T.tipoR, 0) > 0) -- Simples
  OR (N.tipo = 0 AND (N.nfse = 1 OR N.nfse >= 10)) OR (N.tipo = 1 AND N.nfse = 5) OR
                           (IFNULL(CG.storeno, 0) != :loja) OR
                           (N.nfse = 7))) OR 2 NOT IN (0, 999))
GROUP BY N.storeno, N.pdvno, N.xano;

DROP TEMPORARY TABLE IF EXISTS T_EXPEDICAO;
CREATE TEMPORARY TABLE T_EXPEDICAO
SELECT Q.loja,
       Q.separado,
       Q.pdvno,
       Q.xano,
       Q.numero,
       Q.serie,
       Q.pedido,
       Q.cliente,
       Q.nomeCliente,
       Q.valorNota,
       Q.data,
       Q.hora,
       Q.vendedor,
       Q.nomeVendedor,
       Q.nomeCompletoVendedor,
  /*Q.locais,*/
       Q.usuarioExp,
       Q.usuarioCD,
       Q.totalProdutos,
       Q.marca,
       Q.cancelada,
       Q.tipoNotaSaida,
       Q.notaEntrega,
       Q.usuarioEntrega,
       Q.dataEntrega,
       Q.tipo,
       Q.entrega,
       SUM(IFNULL(M.marca, 0) = 2)           AS countEnt,
       SUM(IFNULL(locais, '') LIKE '%CD5A%') AS countCD5A,
       retiraFutura                          AS retiraFutura,
       usernoSingCD,
       usuarioSingCD,
       observacaoPrint,
       observacao
FROM
  T_QUERY                           AS Q
    INNER JOIN sqldados.xaprd2Marca AS M
               ON Q.loja = M.storeno AND
                  Q.pdvno = M.pdvno AND
                  Q.xano = M.xano
WHERE M.marca = 2
GROUP BY Q.loja, Q.pdvno, Q.xano;

INSERT INTO T_KARDEC(loja, prdno, grade, data, doc, nfEnt, tipo, observacao, vencimento, qtde, saldo, userLogin)
SELECT loja                                                    AS loja,
       X.prdno                                                 AS prdno,
       X.grade                                                 AS grade,
       data                                                    AS DATA,
       CONCAT(numero, '/', serie)                              AS doc,
       notaEntrega                                             AS nfEnt,
       IF(tipoNotaSaida = 'ENTRE_FUT', 'ENTREGA', 'EXPEDICAO') AS tipo,
       observacao                                              AS observacao,
       NULL                                                    AS vencimento,
       -X.qtty / 1000                                          AS qtde,
       0                                                       AS saldo,
       IF(tipoNotaSaida = 'ENTRE_FUT', SUBSTRING_INDEX(usuarioCD, '-', 1),
          SUBSTRING_INDEX(usuarioExp, '-', 1))                 AS userLogin
FROM
  T_EXPEDICAO                  AS E
    INNER JOIN sqldados.xaprd2 AS X
               ON X.storeno = E.loja AND X.pdvno = E.pdvno AND X.xano = E.xano
WHERE cancelada = 'N';

/***********************************************************************/

SELECT *
FROM
  T_KARDEC
