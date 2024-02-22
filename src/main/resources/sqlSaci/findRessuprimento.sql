use sqldados;

DO @DATA := SUBDATE(CURRENT_DATE, 30) * 1;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_NOTA;
CREATE TEMPORARY TABLE T_PEDIDO_NOTA
(
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
  AND N.issuedate >= 20240220
GROUP BY ordno;

INSERT IGNORE sqldados.oprdRessu(ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2, icmsSubst,
                                 auxLong3, auxLong4, auxMy3, auxMy4, qtty, qtty_src, qtty_xfr, cost, qttyRcv,
                                 qttyCancel, qttyVendaMes, qttyVendaMesAnt, qttyVendaMedia, qttyPendente, stkDisponivel,
                                 qttyAbc, storeno, seqno, status, bits, bits2, auxShort1, auxShort2, auxShort3,
                                 auxShort4, prdno, grade, remarks, padbyte, gradeFechada, obs, auxStr)
SELECT ordno,
       mult,
       ipi,
       freight,
       icms,
       auxLong1,
       auxLong2,
       auxMy1,
       auxMy2,
       icmsSubst,
       auxLong3,
       auxLong4,
       auxMy3,
       auxMy4,
       qtty,
       qtty_src,
       qtty_xfr,
       cost,
       qttyRcv,
       qttyCancel,
       qttyVendaMes,
       qttyVendaMesAnt,
       qttyVendaMedia,
       qttyPendente,
       stkDisponivel,
       qttyAbc,
       storeno,
       seqno,
       status,
       bits,
       bits2,
       auxShort1,
       auxShort2,
       auxShort3,
       auxShort4,
       prdno,
       grade,
       remarks,
       padbyte,
       gradeFechada,
       obs,
       auxStr
FROM sqldados.oprd
WHERE storeno = 1
  AND (ordno = :ordno OR :ordno = 0)
  AND ordno >= 100000000;

INSERT IGNORE sqldados.ordsRessu(no, date, vendno, discount, amt, package, custo_fin, others,
                                 eord_ordno, dataFaturamento, invno, freightAmt, auxLong1, auxLong2, amtOrigem,
                                 dataEntrega, discountOrig, l1, l2, l3, l4, m1, m2, m3, m4, deliv, storeno, carrno,
                                 empno, prazo, eord_storeno, delivOriginal, bits, bits2, bits3, padbyte, indxno, repno,
                                 auxShort1, auxShort2, noofinst, status, s1, s2, s3, s4, frete, remarks, ordnoFromVend,
                                 remarksInv, remarksRcv, remarksOrd, auxChar, c1, c2, c3, c4)
SELECT no,
       date,
       vendno,
       discount,
       amt,
       package,
       custo_fin,
       others,
       eord_ordno,
       dataFaturamento,
       invno,
       freightAmt,
       auxLong1,
       auxLong2,
       amtOrigem,
       dataEntrega,
       discountOrig,
       l1,
       l2,
       l3,
       l4,
       m1,
       m2,
       m3,
       m4,
       deliv,
       storeno,
       carrno,
       empno,
       prazo,
       eord_storeno,
       delivOriginal,
       bits,
       bits2,
       bits3,
       padbyte,
       indxno,
       repno,
       auxShort1,
       auxShort2,
       noofinst,
       status,
       s1,
       s2,
       s3,
       s4,
       frete,
       remarks,
       ordnoFromVend,
       remarksInv,
       remarksRcv,
       remarksOrd,
       auxChar,
       c1,
       c2,
       c3,
       c4
FROM sqldados.ords o
WHERE storeno = 1
  AND (no = :ordno OR :ordno = 0)
  AND no >= 100000000;

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
       NF.dataNota                                        AS dataBaixa,
       N.s4                                               AS singno,
       SU.name                                            AS sing
FROM sqldados.ords AS N
       LEFT JOIN T_PEDIDO_NOTA AS NF
                 ON N.no = NF.ordno
       INNER JOIN sqldados.oprd AS X
                  ON N.storeno = X.storeno AND N.no = X.ordno
       LEFT JOIN sqldados.prdloc AS L
                 ON L.prdno = X.prdno AND L.storeno = 4
       LEFT JOIN sqldados.users AS SU
                 ON N.s4 = SU.no
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
       NF.dataNota                                        AS dataBaixa,
       N.s4                                               AS singno,
       SU.name                                            AS sing
FROM sqldados.ordsRessu AS N
       LEFT JOIN T_PEDIDO_NOTA AS NF
                 ON N.no = NF.ordno
       INNER JOIN sqldados.oprdRessu AS X
                  ON N.storeno = X.storeno AND N.no = X.ordno
       LEFT JOIN sqldados.prdloc AS L
                 ON L.prdno = X.prdno AND L.storeno = 4
       LEFT JOIN sqldados.users AS SU
                 ON N.s4 = SU.no
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
       dataBaixa,
       singno,
       sing
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
       dataBaixa,
       singno,
       sing
FROM T_PEDIDO_02;

SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       usuarioCD,
       totalProdutos,
       marca,
       cancelada,
       MAX(notaBaixa) AS notaBaixa,
       MAX(dataBaixa) AS dataBaixa,
       singno         AS singno,
       sing           AS sing
FROM T_PEDIDO AS D
GROUP BY numero,
         IF(:marca = 999, '', SUBSTRING_INDEX(usuarioCD, '-', 1)),
         IF(:marca = 999, '', localizacao)
