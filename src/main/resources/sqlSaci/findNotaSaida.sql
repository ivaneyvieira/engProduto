USE sqldados;


/*

SHOW INDEX FROM sqldados.nf

CREATE INDEX e6 ON sqldados.nf (l16)

 */
/*
 SHOW INDEX FROM sqldados.nf

DROP INDEX e6 ON sqldados.nf
CREATE INDEX e6 ON sqldados.nf (l16, issuedate)
*/

DO @DT := 20070101;

DROP TEMPORARY TABLE IF EXISTS T_TIPO;
CREATE TEMPORARY TABLE T_TIPO
(
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno AS storeno, ordno AS ordno, SUM((E.bits & 2) > 0) AS tipoR, SUM((E.bits & 2) = 0) AS tipoE
FROM
  sqldados.eoprdf AS E
WHERE (storeno IN (2, 3, 4, 5, 8))
  AND (`date` >= @DT)
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
  AND P.date >= @DT
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
  AND `date` >= @DT
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

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno AS prdno, A.grade AS grade, TRIM(MID(A.localizacao, 1, 4)) AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE ((TRIM(MID(A.localizacao, 1, 4)) IN (:local)) OR ('TODOS' IN (:local)) OR (A.localizacao = ''))
  AND (A.storeno = 4)
  AND (A.prdno = :prdno OR :prdno = '')
  AND (A.grade = :grade OR :grade = '');

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

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
  AND `date` > @DT
  AND optionEntrega % 10 = 4
  AND nfse != 3
GROUP BY storeno, pdvno, xano;

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT N.storeno                                                              AS loja,
       N.s14 != 0                                                             AS separado,
       N.pdvno                                                                AS pdvno,
       N.xano                                                                 AS xano,
       N.nfno                                                                 AS numero,
       N.nfse                                                                 AS serie,
       N.eordno                                                               AS pedido,
       N.custno                                                               AS cliente,
       IFNULL(C.name, '')                                                     AS nomeCliente,
       N.grossamt / 100                                                       AS valorNota,
       CAST(N.issuedate AS DATE)                                              AS data,
       SEC_TO_TIME(P.time)                                                    AS hora,
       N.empno                                                                AS vendedor,
       TRIM(MID(E.sname, 1, 20))                                              AS nomeVendedor,
       TRIM(E.name)                                                           AS nomeCompletoVendedor,
       GROUP_CONCAT(DISTINCT IF(LC.localizacao = '', NULL, LC.localizacao))   AS locais,
       MAX(X.c5)                                                              AS usuarioExp,
       MAX(X.c4)                                                              AS usuarioCD,
       SUM((X.qtty / 1000) * X.preco)                                         AS totalProdutos,
       MAX(IFNULL(M.marca, 0))                                                AS marca,
       IF(N.status <> 1, 'N', 'S')                                            AS cancelada,
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
       END                                                                    AS tipoNotaSaida,
       IFNULL(ENT.notaEntrega, '')                                            AS notaEntrega,
       ENT.usuario                                                            AS usuarioEntrega,
       CAST(ENT.dataEntrega AS DATE)                                          AS dataEntrega,
       CASE
         WHEN IFNULL(T.tipoE, 0) > 0 AND IFNULL(T.tipoR, 0) = 0 THEN 'Entrega'
         WHEN IFNULL(T.tipoE, 0) = 0 AND IFNULL(T.tipoR, 0) > 0 THEN 'Retira'
         WHEN IFNULL(T.tipoE, 0) > 0 AND IFNULL(T.tipoR, 0) > 0 THEN 'Misto'
                                                                ELSE ''
       END                                                                    AS tipo,
       (IFNULL(CG.storeno, :loja) != :loja) OR (N.storeno = :loja)            AS retiraFutura,
       IF(AR.city = 'TIMON', 'Timon', AR.name)                                AS rota,
       CA.addr                                                                AS enderecoCliente,
       CA.nei                                                                 AS bairroCliente,
       IF(LEFT(OBS.remarks__480, 2) = 'EF ', LEFT(OBS.remarks__480, 11), ' ') AS agendado,
       CAST(IF(N.l16 = 0, NULL, N.l16) AS DATE)                               AS entrega,
       M.no                                                                   AS empnoMotorista,
       M.sname                                                                AS nomeMotorista,
       EP.no                                                                  AS usernoPrint,
       EP.login                                                               AS usuarioPrint,
       MAX(EC.no)                                                             AS usernoSingCD,
       GROUP_CONCAT(DISTINCT IF(EC.login = '', '', EC.login))                 AS usuarioSingCD,
       MAX(EE.no)                                                             AS usernoSingExp,
       GROUP_CONCAT(DISTINCT IF(EE.login = '', '', EE.login))                 AS usuarioSingExp,
       MAX(IF('CD5A' = IFNULL(LC.localizacao, ''), IFNULL(X.c3, ''), ''))     AS usuarioSep,
       print_remarks                                                          AS observacaoPrint
FROM
  sqldados.nf                       AS N
    LEFT JOIN  sqldados.nfUserPrint AS PT
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.users       AS EP
               ON EP.no = PT.userno
    LEFT JOIN  T_CARGA              AS CG
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqlpdv.pxa           AS P
               USING (storeno, pdvno, xano)
    LEFT JOIN  T_ENTREGA            AS ENT
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.xaprd2      AS X
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.xaprd2Marca AS M
               ON X.storeno = M.storeno AND
                  X.pdvno = M.pdvno AND
                  X.xano = M.xano AND
                  X.prdno = M.prdno AND
                  X.grade = M.grade
    LEFT JOIN  sqldados.users       AS EC
               ON EC.no = X.s4
    LEFT JOIN  sqldados.users       AS EE
               ON EE.no = X.s5
    LEFT JOIN  T_TIPO               AS T
               ON N.storeno = T.storeno AND N.eordno = T.ordno
    LEFT JOIN  T_LOC                AS LC
               ON LC.prdno = X.prdno AND LC.grade = X.grade
    LEFT JOIN  sqldados.emp         AS E
               ON E.no = N.empno
    LEFT JOIN  sqldados.custp       AS C
               ON C.no = N.custno
    LEFT JOIN  sqldados.ctadd       AS CA
               ON CA.custno = N.custno AND CA.seqno = N.custno_addno
    LEFT JOIN  sqldados.route       AS RT
               ON RT.no = CA.routeno
    LEFT JOIN  sqldados.area        AS AR
               ON AR.no = RT.areano
    LEFT JOIN  sqldados.eordrk      AS OBS
               ON (OBS.storeno = N.storeno AND OBS.ordno = N.eordno)
    LEFT JOIN  sqldados.emp         AS M
               ON N.s16 = M.no
WHERE (N.l16 >= :dataEntregaInicial OR :dataEntregaInicial = 0)
  AND (N.l16 <= :dataEntregaFinal OR :dataEntregaFinal = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.issuedate >= @DT
  AND (X.prdno = :prdno OR :prdno = '')
  AND (X.grade = :grade OR :grade = '')
  AND ((:loja = 0 OR (N.storeno != :loja AND IFNULL(tipoR, 0) = 0 AND N.tipo NOT IN (0, 1)) OR
        (N.storeno = :loja OR (IFNULL(CG.storeno, 0) != :loja AND IFNULL(CG.storeno, 0) != 0))))
  AND ((:marca IN (0, 999) AND ((N.tipo = 4 AND IFNULL(T.tipoE, 0) > 0) -- Retira Futura
  OR (N.tipo = 3 AND IFNULL(T.tipoR, 0) > 0) -- Simples
  OR (N.tipo = 0 AND (N.nfse = 1 OR N.nfse >= 10)) OR (N.tipo = 1 AND N.nfse = 5) OR (IFNULL(CG.storeno, 0) != :loja) OR
                                (N.nfse = 7))) OR :marca NOT IN (0, 999))
  AND ('TODOS' IN (:local) OR LC.localizacao IN (:local))
GROUP BY N.storeno, N.pdvno, N.xano;

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
       Q.rota,
       Q.agendado,
       Q.entrega,
       Q.enderecoCliente,
       Q.bairroCliente,
       SUM(IFNULL(M.marca, 0) = 0)           AS countExp,
       SUM(IFNULL(M.marca, 0) = 1)           AS countCD,
       SUM(IFNULL(M.marca, 0) = 2)           AS countEnt,
       SUM(IFNULL(M.impresso, 0) = 1)        AS countImp,
       SUM(IFNULL(M.impresso, 0) = 0)        AS countNImp,
       SUM(IFNULL(locais, '') LIKE '%CD5A%') AS countCD5A,
       retiraFutura                          AS retiraFutura,
       empnoMotorista,
       nomeMotorista,
       usernoPrint,
       usuarioPrint,
       usernoSingCD,
       usuarioSingCD,
       usernoSingExp,
       usuarioSingExp,
       usuarioSep,
       observacaoPrint
FROM
  T_QUERY                           AS Q
    INNER JOIN sqldados.xaprd2      AS X
               ON X.storeno = Q.loja AND
                  X.pdvno = Q.pdvno AND
                  X.xano = Q.xano
    LEFT JOIN  sqldados.xaprd2Marca AS M
               ON X.storeno = M.storeno AND
                  X.pdvno = M.pdvno AND
                  X.xano = M.xano AND
                  X.prdno = M.prdno AND
                  X.grade = M.grade
WHERE (@PESQUISA = '' OR numero LIKE @PESQUISA_START OR notaEntrega LIKE @PESQUISA_START OR cliente = @PESQUISA_NUM OR
       nomeCliente LIKE @PESQUISA_LIKE OR vendedor = @PESQUISA_NUM OR nomeVendedor LIKE @PESQUISA_LIKE OR
       nomeMotorista LIKE @PESQUISA_LIKE OR usuarioPrint LIKE @PESQUISA_LIKE OR usuarioSingCD LIKE @PESQUISA_LIKE OR
       pedido LIKE @PESQUISA OR locais LIKE @PESQUISA_LIKE)
GROUP BY Q.loja, Q.pdvno, Q.xano
HAVING ((:marca = 0 AND countExp > 0) OR (:marca = 1 AND countCD > 0) OR (:marca = 2 AND countEnt > 0) OR
        (:marca = 999))