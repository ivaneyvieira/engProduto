USE sqldados;

DO @DT := 20240322;

DROP TEMPORARY TABLE IF EXISTS T_E;
CREATE TEMPORARY TABLE T_E
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
       P.eordno                                  AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       P.date                                    AS data
FROM sqlpdv.pxa AS P
WHERE P.cfo IN (5117, 6117)
  AND storeno IN (2, 3, 4, 5)
  AND date >= @DT
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
  AND storeno IN (2, 3, 4, 5)
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
       V.numero      AS notaVenda,
       MAX(E.numero) AS notaEntrega,
       MAX(E.data)   AS dataEntrega
FROM T_V AS V
       LEFT JOIN T_E AS E
                 USING (storeno, ordno)
GROUP BY V.storeno, V.pdvno, V.xano;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, loc)
)
SELECT P.no                                                                     AS prdno,
       IF(:marca = 999, '', CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR)) AS loc
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdloc AS L
                 ON P.no = L.prdno
WHERE (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
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

SELECT N.storeno                          AS loja,
       N.pdvno                            AS pdvno,
       N.xano                             AS xano,
       N.nfno                             AS numero,
       N.nfse                             AS serie,
       N.custno                           AS cliente,
       IFNULL(C.name, '')                 AS nomeCliente,
       CAST(issuedate AS DATE)            AS data,
       SEC_TO_TIME(P.time)                AS hora,
       N.empno                            AS vendedor,
       TRIM(MID(E.sname, 1, 20))          AS nomeVendedor,
       CAST(IFNULL(L.locais, '') AS CHAR) AS locais,
       X.c5                               AS usuarioExp,
       X.c4                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.preco)     AS totalProdutos,
       MAX(X.s12)                         AS marca,
       IF(N.status <> 1, 'N', 'S')        AS cancelada,
       CASE
         WHEN tipo = 0
           THEN ''
         WHEN tipo = 1
           THEN ''
         WHEN tipo = 2
           THEN 'DEVOLUCAO'
         WHEN tipo = 3
           THEN 'SIMP REME'
         WHEN tipo = 4
           THEN 'ENTRE FUT'
         WHEN tipo = 5
           THEN 'RET DEMON'
         WHEN tipo = 6
           THEN 'VENDA USA'
         WHEN tipo = 7
           THEN 'OUTROS'
         WHEN tipo = 8
           THEN 'NF CF'
         WHEN tipo = 9
           THEN 'PERD/CONSER'
         WHEN tipo = 10
           THEN 'REPOSICAO'
         WHEN tipo = 11
           THEN 'RESSARCI'
         WHEN tipo = 12
           THEN 'COMODATO'
         WHEN tipo = 13
           THEN 'NF EMPRESA'
         WHEN tipo = 14
           THEN 'BONIFICA'
         WHEN tipo = 15
           THEN 'NFE'
         ELSE ''
       END                                AS tipoNotaSaida,
       IFNULL(ENT.notaEntrega, '')        AS notaEntrega,
       CAST(ENT.dataEntrega AS DATE)      AS dataEntrega
FROM sqldados.nf AS N
       LEFT JOIN sqlpdv.pxa AS P
                 USING (storeno, pdvno, xano)
       LEFT JOIN T_ENTREGA AS ENT
                 USING (storeno, pdvno, xano)
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.nfrprd AS NP
                 ON X.storeno = NP.storeno AND X.pdvno = NP.pdvno AND X.xano = NP.xano AND
                    X.prdno = NP.prdno AND X.grade = NP.grade
       INNER JOIN T_LOCPRD AS L
                  ON L.prdno = X.prdno
       LEFT JOIN sqldados.emp AS E
                 ON E.no = N.empno
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
WHERE (issuedate >= :dataInicial OR :dataInicial = 0)
  AND (issuedate <= :dataFinal OR :dataFinal = 0)
  AND issuedate >= @DT
  AND (CASE
         WHEN (IFNULL(NP.optionEntrega, 0) % 100) = 4
           THEN 'RETIRAF'
         WHEN (N.nfse = 1 AND N.cfo IN (5922, 6922)) OR (N.nfse = 7)
           THEN 'VENDAF'
         WHEN N.nfse = '66'
           THEN 'ACERTO_S'
         WHEN N.nfse = '3'
           THEN 'ENT_RET'
         WHEN tipo = 0 AND N.nfse >= 10
           THEN 'NFCE'
         WHEN tipo = 0
           THEN 'VENDA'
         WHEN tipo = 1
           THEN 'TRANSFERENCIA'
         WHEN tipo = 2
           THEN 'DEV_FOR'
         WHEN tipo = 3
           THEN 'OUTRAS_NFS'
         WHEN tipo = 7
           THEN 'OUTRAS_NFS'
         ELSE 'SP_REME'
       END IN (:listaTipos) OR 'TODOS' IN (:listaTipos))
  AND (CASE :tipoNota
         WHEN 0 THEN tipo = 0 AND N.nfse >= 10
         WHEN 1 THEN !(tipo = 0 AND N.nfse >= 10)
         WHEN 999 THEN TRUE
         ELSE FALSE
       END
  )
  AND (X.s12 = :marca OR :marca = 999)

AND case :notaEntrega
         WHEN 'S' THEN (N.storeno != :loja OR :loja = 0) AND N.nfse = '3'
         WHEN 'N' THEN (N.storeno = :loja OR :loja = 0)
         ELSE FALSE
       END
GROUP BY N.storeno,
         N.pdvno,
         N.xano,
         IF(:marca = 999, '', SUBSTRING_INDEX(X.c5, '-', 1)),
         IF(:marca = 999, '', SUBSTRING_INDEX(X.c4, '-', 1))
HAVING (
         @PESQUISA = '' OR
         numero LIKE @PESQUISA_START OR
         notaEntrega LIKE @PESQUISA_START OR
         cliente = @PESQUISA_NUM OR
         nomeCliente LIKE @PESQUISA_LIKE OR
         vendedor = @PESQUISA_NUM OR
         nomeVendedor LIKE @PESQUISA_LIKE OR
         locais LIKE @PESQUISA_LIKE
         )
