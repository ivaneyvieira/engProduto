DO @DATA := SUBDATE(CURRENT_DATE, 30) * 1;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_NOTA;
CREATE TEMPORARY TABLE T_PEDIDO_NOTA (
  PRIMARY KEY (ordno)
)
SELECT N.l2                                      AS ordno,
       N.storeno,
       N.pdvno,
       N.custno,
       N.xano,
       N.storeno                                 AS storenoNota,
       N.empno,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR) AS numero,
       N.issuedate                               AS date,
       CAST(N.issuedate AS DATE)                 AS dataNota
FROM sqldados.nf AS N
WHERE N.l2 BETWEEN 100000000 AND 999999999
  AND N.issuedate >= @DATA
GROUP BY ordno;


DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_00;
CREATE TEMPORARY TABLE T_PEDIDO_00
SELECT NF.ordno                                           AS numero,
       0                                                  AS fornecedor,
       CAST(NF.date AS DATE)                              AS data,
       NF.empno                                           AS comprador,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.obs                                              AS usuarioCD,
       SUM((X.qtty / 1000) * X.cost)                      AS totalProdutos,
       MAX(X.auxShort4)                                   AS marca,
       'N'                                                AS cancelada,
       CAST(IFNULL(NF.numero, '') AS CHAR)                AS notaBaixa,
       NF.dataNota                                        AS dataBaixa
FROM T_PEDIDO_NOTA           AS NF
  INNER JOIN sqldados.oprd   AS X
	       ON NF.storeno = X.storeno AND NF.ordno = X.ordno
  LEFT JOIN  sqldados.prdloc AS L
	       ON L.prdno = X.prdno AND L.storeno = 4
WHERE NF.date >= @DATA
  AND (X.auxShort4 = :marca OR :marca = 999)
  AND (NF.ordno = :ordno OR :ordno = 0)
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY NF.storeno,
	 NF.ordno,
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.obs, '-', 1)),
	 IF(:marca = 999, '', MID(L.localizacao, 1, 4));

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_01;
CREATE TEMPORARY TABLE T_PEDIDO_01
SELECT N.no                                               AS numero,
       vendno                                             AS fornecedor,
       CAST(NF.date AS DATE)                              AS data,
       N.empno                                            AS comprador,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.obs                                              AS usuarioCD,
       SUM((X.qtty / 1000) * X.cost)                      AS totalProdutos,
       MAX(X.auxShort4)                                   AS marca,
       'N'                                                AS cancelada,
       CAST(IFNULL(NF.numero, '') AS CHAR)                AS notaBaixa,
       NF.dataNota                                        AS dataBaixa
FROM sqldados.ords           AS N
  LEFT JOIN  T_PEDIDO_NOTA   AS NF
	       ON N.no = NF.ordno
  INNER JOIN sqldados.oprd   AS X
	       ON N.storeno = X.storeno AND N.no = X.ordno
  LEFT JOIN  sqldados.prdloc AS L
	       ON L.prdno = X.prdno AND L.storeno = 4
WHERE N.date >= @DATA
  AND (X.auxShort4 = :marca OR :marca = 999)
  AND (N.storeno = 1)
  AND (N.no = :ordno OR :ordno = 0)
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
  AND N.no >= 100000000
  AND N.date >= @DATA
GROUP BY N.storeno,
	 N.no,
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.obs, '-', 1)),
	 IF(:marca = 999, '', MID(L.localizacao, 1, 4));

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_02;
CREATE TEMPORARY TABLE T_PEDIDO_02
SELECT N.no                                               AS numero,
       vendno                                             AS fornecedor,
       CAST(N.date AS DATE)                               AS data,
       N.empno                                            AS comprador,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.obs                                              AS usuarioCD,
       SUM((X.qtty / 1000) * X.cost)                      AS totalProdutos,
       MAX(X.auxShort4)                                   AS marca,
       'N'                                                AS cancelada,
       CAST(IFNULL(NF.numero, '') AS CHAR)                AS notaBaixa,
       NF.dataNota                                        AS dataBaixa
FROM sqldados.ordsRessu         AS N
  LEFT JOIN  T_PEDIDO_NOTA      AS NF
	       ON N.no = NF.ordno
  INNER JOIN sqldados.oprdRessu AS X
	       ON N.storeno = X.storeno AND N.no = X.ordno
  LEFT JOIN  sqldados.prdloc    AS L
	       ON L.prdno = X.prdno AND L.storeno = 4
WHERE N.date >= @DATA
  AND (X.auxShort4 = :marca OR :marca = 999)
  AND (N.storeno = 1)
  AND (N.no = :ordno OR :ordno = 0)
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
  AND N.no >= 100000000
GROUP BY N.storeno,
	 N.no,
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.obs, '-', 1)),
	 IF(:marca = 999, '', MID(L.localizacao, 1, 4));

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE TEMPORARY TABLE T_PEDIDO
SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       usuarioCD,
       totalProdutos,
       marca,
       cancelada,
       notaBaixa,
       dataBaixa
FROM T_PEDIDO_01
UNION
DISTINCT
SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       usuarioCD,
       totalProdutos,
       marca,
       cancelada,
       notaBaixa,
       dataBaixa
FROM T_PEDIDO_02
UNION
DISTINCT
SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       usuarioCD,
       totalProdutos,
       marca,
       cancelada,
       notaBaixa,
       dataBaixa
FROM T_PEDIDO_00;

SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       usuarioCD,
       totalProdutos,
       marca,
       cancelada,
       max(notaBaixa) AS notaBaixa,
       max(dataBaixa) AS dataBaixa
FROM T_PEDIDO AS D
GROUP BY numero,
	 IF(:marca = 999, '', SUBSTRING_INDEX(usuarioCD, '-', 1)),
	 IF(:marca = 999, '', localizacao)
