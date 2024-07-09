USE sqldados;

DO @DT := 20240401;

DROP TEMPORARY TABLE IF EXISTS T_TIPO;
CREATE TEMPORARY TABLE T_TIPO
(
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno                     AS storeno,
       ordno                       AS ordno,
       SUM(E.bits & POW(2, 1))     AS tipoR,
       SUM(NOT E.bits & POW(2, 1)) AS tipoE
FROM sqldados.eoprdf AS E
WHERE (storeno IN (2, 3, 4, 5, 8))
  AND (date >= @DT)
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
FROM sqlpdv.pxa AS P
       LEFT JOIN sqldados.users AS U
                 ON U.no = P.s3
WHERE P.cfo IN (5117, 6117)
  AND storeno IN (2, 3, 4, 5, 6, 7, 8)
  AND DATE >= @DT
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
FROM sqlpdv.pxa AS P
WHERE P.cfo IN (5922, 6922)
  AND storeno IN (2, 3, 4, 5, 6, 7, 8)
  AND nfse = '1'
  AND date >= @DT
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
FROM T_V AS V
       LEFT JOIN T_E AS E
                 USING (storeno, ordno)
GROUP BY V.storeno, V.pdvno, V.xano;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, loc)
)
SELECT P.no                                                                AS prdno,
       CAST(MID(COALESCE(A.localizacao, L.localizacao, ''), 1, 4) AS CHAR) AS loc
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdloc AS L
                 ON P.no = L.prdno
       LEFT JOIN sqldados.prdAdicional AS A
                 ON A.prdno = L.prdno
                   AND A.grade = L.grade
                   AND A.storeno = L.storeno
WHERE (MID(COALESCE(A.localizacao, L.localizacao, ''), 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
  AND (L.storeno = :lojaLocal OR :lojaLocal = 0)
GROUP BY prdno, loc;


DROP TEMPORARY TABLE IF EXISTS T_LOCPRD;
CREATE TEMPORARY TABLE T_LOCPRD
(
  PRIMARY KEY (prdno)
)
SELECT prdno, CAST(GROUP_CONCAT(loc) AS CHAR) AS locais
FROM T_LOC
GROUP BY prdno;

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
FROM sqldados.nfrprd
WHERE (storenoStk = :loja OR :loja = 0)
  AND storeno != storenoStk
  AND date > 20240401
  AND optionEntrega % 10 = 4
  AND nfse != 3
GROUP BY storeno, pdvno, xano;

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT N.storeno                                                              AS loja,
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
       CAST(IFNULL(L.locais, '') AS CHAR)                                     AS locais,
       X.c5                                                                   AS usuarioExp,
       X.c4                                                                   AS usuarioCD,
       SUM((X.qtty / 1000) * X.preco)                                         AS totalProdutos,
       MAX(X.s11)                                                             AS marca,
       IF(N.status <> 1, 'N', 'S')                                            AS cancelada,
       CASE
         WHEN N.nfse = 7
           THEN 'ENTREGA_WEB'
         WHEN tipo = 0 AND N.nfse >= 10
           THEN 'NFCE'
         WHEN tipo = 0 AND N.nfse < 10
           THEN 'NFE'
         WHEN N.tipo = 1
           THEN 'TRANSFERENCIA'
         WHEN N.tipo = 2
           THEN 'DEVOLUCAO'
         WHEN N.tipo = 3
           THEN IF(N.storeno != :loja AND :loja != 0 AND N.nfse = 3, '', 'SIMP_REME')
         WHEN N.tipo = 4
           THEN IF(IFNULL(T.tipoE, 0) = 0 AND IFNULL(T.tipoR, 0) > 0,
                   IF(N.storeno != :loja AND :loja != 0 AND N.nfse = 3, '', 'SIMP_REME'),
                   'ENTRE_FUT')
         WHEN N.tipo = 5
           THEN 'RET_DEMON'
         WHEN N.tipo = 6
           THEN 'VENDA_USA'
         WHEN N.tipo = 7
           THEN 'OUTROS'
         WHEN N.tipo = 8
           THEN 'NF_CF'
         WHEN N.tipo = 9
           THEN 'PERD/CONSER'
         WHEN N.tipo = 10
           THEN 'REPOSICAO'
         WHEN N.tipo = 11
           THEN 'RESSARCI'
         WHEN N.tipo = 12
           THEN 'COMODATO'
         WHEN N.tipo = 13
           THEN 'NF_EMPRESA'
         WHEN N.tipo = 14
           THEN 'BONIFICA'
         WHEN N.tipo = 15
           THEN 'NFE'
         ELSE ''
       END                                                                    AS tipoNotaSaida,
       IFNULL(ENT.notaEntrega, '')                                            AS notaEntrega,
       ENT.usuario                                                            AS usuarioEntrega,
       CAST(ENT.dataEntrega AS DATE)                                          AS dataEntrega,
       CASE
         WHEN IFNULL(T.tipoE, 0) > 0
           AND IFNULL(T.tipoR, 0) = 0 THEN 'Entrega'
         WHEN IFNULL(T.tipoE, 0) = 0
           AND IFNULL(T.tipoR, 0) > 0 THEN 'Retira'
         WHEN IFNULL(T.tipoE, 0) > 0
           AND IFNULL(T.tipoR, 0) > 0 THEN 'Misto'
         ELSE ''
       END                                                                    AS tipo,
       X.c5,
       X.c4,
       IFNULL(CG.storeno, :loja) != :loja || N.storeno = :loja                AS retiraFutura,
       IF(AR.city = 'TIMON', 'Timon', AR.name)                                AS rota,
       CA.addr                                                                AS enderecoCliente,
       CA.nei                                                                 AS bairroCliente,
       IF(LEFT(OBS.remarks__480, 2) = 'EF ', LEFT(OBS.remarks__480, 11), ' ') AS agendado
FROM sqldados.nf AS N
       LEFT JOIN T_CARGA AS CG
                 USING (storeno, pdvno, xano)
       LEFT JOIN sqlpdv.pxa AS P
                 USING (storeno, pdvno, xano)
       LEFT JOIN T_ENTREGA AS ENT
                 USING (storeno, pdvno, xano)
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
       LEFT JOIN T_TIPO AS T
                 ON N.storeno = T.storeno AND
                    N.eordno = T.ordno
       INNER JOIN T_LOCPRD AS L
                  ON L.prdno = X.prdno
       LEFT JOIN sqldados.emp AS E
                 ON E.no = N.empno
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
       LEFT JOIN sqldados.ctadd AS CA
                 ON CA.custno = N.custno
                   AND CA.seqno = N.custno_addno
  /*LEFT JOIN sqldados.rotasAdd AS RA
            ON RA.cidade = CA.city
              AND RA.bairro = CA.nei*/
       LEFT JOIN sqldados.route AS RT
                 ON RT.no = CA.routeno
/*       LEFT JOIN sqldados.rotasAdd AS RA
                 ON RA.cidade = 'TERESINA'
                   AND RA.bairro = TRIM(MID(RT.name, LOCATE(' ', RT.name, 1), 100))*/
       LEFT JOIN sqldados.area AS AR
                 ON AR.no = RT.areano
       LEFT JOIN sqldados.eordrk AS OBS
                 ON (OBS.storeno = N.storeno AND OBS.ordno = N.eordno)
WHERE (issuedate >= :dataInicial OR :dataInicial = 0)
  AND (issuedate <= :dataFinal OR :dataFinal = 0)
  AND issuedate >= @DT
  AND (X.s11 = :marca OR :marca = 999)
  AND CASE :notaEntrega
        WHEN 'S' THEN (N.storeno != :loja OR :loja = 0)
          AND IFNULL(tipoR, 0) = 0
          AND N.tipo NOT IN (0, 1)
        WHEN 'N' THEN (N.storeno = :loja OR :loja = 0) OR
                      (IFNULL(CG.storeno, 0) != :loja AND IFNULL(CG.storeno, 0) != 0)
        ELSE FALSE
      END
  AND CASE
        WHEN :marca IN (0, 999) THEN (((N.tipo IN (4)) AND IFNULL(tipoE, 0) > 0)/*Retira Futura*/
          OR ((N.tipo IN (3)) AND IFNULL(tipoR, 0) > 0)/*Simples*/
          OR (N.tipo = 0 AND N.nfse = 1)
          OR (N.tipo = 0 AND N.nfse >= 10)
          OR (N.tipo = 1 AND N.nfse = 5)
          OR (IFNULL(CG.storeno, 0) != :loja)
          OR (N.nfse = 7)
          )
        ELSE TRUE
      END
GROUP BY N.storeno,
         N.pdvno,
         N.xano,
         SUBSTRING_INDEX(X.c5, '-', 1),
         SUBSTRING_INDEX(X.c4, '-', 1)
HAVING (@PESQUISA = ''
  OR numero LIKE @PESQUISA_START
  OR notaEntrega LIKE @PESQUISA_START
  OR cliente = @PESQUISA_NUM
  OR nomeCliente LIKE @PESQUISA_LIKE
  OR vendedor = @PESQUISA_NUM
  OR nomeVendedor LIKE @PESQUISA_LIKE
  OR locais LIKE @PESQUISA_LIKE);

SELECT Q.loja,
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
       Q.locais,
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
       Q.enderecoCliente,
       Q.bairroCliente,
       SUM(X.s11 = 0) AS countExp,
       SUM(X.s11 = 1) AS countCD,
       SUM(X.s11 = 2) AS countEnt,
       SUM(X.s10 = 1) AS countImp,
       SUM(X.s10 = 0) AS countNImp,
       retiraFutura   AS retiraFutura
FROM T_QUERY AS Q
       INNER JOIN sqldados.xaprd2 AS X
                  ON X.storeno = Q.loja
                    AND X.pdvno = Q.pdvno
                    AND X.xano = Q.xano
GROUP BY Q.loja, Q.pdvno, Q.xano
