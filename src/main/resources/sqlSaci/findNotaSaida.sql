USE sqldados;

DO @DT := 20220201;

DROP TEMPORARY TABLE IF EXISTS T_E;
CREATE TEMPORARY TABLE T_E (
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
CREATE TEMPORARY TABLE T_V (
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
CREATE TEMPORARY TABLE T_ENTREGA (
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT V.storeno,
       V.pdvno,
       V.xano,
       V.numero      AS notaVenda,
       MAX(E.numero) AS notaEntrega,
       MAX(E.data)   AS dataEntrega
FROM T_V        AS V
  LEFT JOIN T_E AS E
	      USING (storeno, ordno)
GROUP BY V.storeno, V.pdvno, V.xano;

SELECT N.storeno                                          AS loja,
       N.pdvno                                            AS pdvno,
       N.xano                                             AS xano,
       N.nfno                                             AS numero,
       N.nfse                                             AS serie,
       custno                                             AS cliente,
       CAST(issuedate AS DATE)                            AS data,
       N.empno                                            AS vendedor,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.c5                                               AS usuarioExp,
       X.c4                                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.preco)                     AS totalProdutos,
       MAX(X.s12)                                         AS marca,
       IF(N.status <> 1, 'N', 'S')                        AS cancelada,
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
       END                                                AS tipoNotaSaida,
       IFNULL(ENT.notaEntrega, '')                        AS notaEntrega,
       CAST(ENT.dataEntrega AS DATE)                      AS dataEntrega
FROM sqldados.nf             AS N
  LEFT JOIN  T_ENTREGA       AS ENT
	       USING (storeno, pdvno, xano)
  INNER JOIN sqldados.xaprd2 AS X
	       USING (storeno, pdvno, xano)
  LEFT JOIN  sqldados.nfrprd AS NP
	       ON X.storeno = NP.storeno AND X.pdvno = NP.pdvno AND X.xano = NP.xano AND
		  X.prdno = NP.prdno AND X.grade = NP.grade AND (optionEntrega % 100) = 4
  LEFT JOIN  sqldados.prdloc AS L
	       ON L.prdno = X.prdno AND L.storeno = :storeno
  LEFT JOIN  sqldados.emp    AS E
	       ON E.no = N.empno
WHERE N.issuedate >= @DT
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
       END NOT IN (:listaTipos))
  AND (X.s12 = :marca OR :marca = 999)
  AND (N.storeno = :storeno OR :storeno = 0)
  AND (N.nfno = :nfno OR :nfno = 0)
  AND (N.nfse = :nfse OR :nfse = '')
  AND (N.custno = :cliente OR :cliente = 0)
  AND (E.sname = :vendedor OR :vendedor = '')
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY N.storeno,
	 N.pdvno,
	 N.xano,
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.c5, '-', 1)),
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.c4, '-', 1)),
	 IF(:marca = 999, '', MID(L.localizacao, 1, 4))
