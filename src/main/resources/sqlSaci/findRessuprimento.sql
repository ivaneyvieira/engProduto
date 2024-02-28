USE sqldados;

DO @DATA := SUBDATE(CURRENT_DATE, 30) * 1;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT L.prdno                                                                              AS prdno,
       L.grade                                                                              AS grade,
       CAST(MID(COALESCE(A1.localizacao, A2.localizacao, L.localizacao, ''), 1, 4) AS CHAR) AS localizacao
FROM sqldados.prdloc AS L
       LEFT JOIN sqldados.prdAdicional AS A1
                 USING (storeno, prdno, grade)
       LEFT JOIN sqldados.prdAdicional AS A2
                 USING (storeno, prdno)
WHERE storeno = 4
GROUP BY L.prdno, L.grade;

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
  AND (N.l2 LIKE CONCAT(:lojaRessu, '%') OR :lojaRessu = 0)
  AND N.issuedate >= @DATA
  AND N.issuedate >= 20240226
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

INSERT IGNORE sqldados.ordsRessu(no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno,
                                 dataFaturamento, invno, freightAmt, auxLong1, auxLong2, amtOrigem, dataEntrega,
                                 discountOrig, l1, l2, l3, l4, m1, m2, m3, m4, deliv, storeno, carrno, empno, prazo,
                                 eord_storeno, delivOriginal, bits, bits2, bits3, padbyte, indxno, repno, auxShort1,
                                 auxShort2, noofinst, status, s1, s2, s3, s4, frete, remarks, ordnoFromVend, remarksInv,
                                 remarksRcv, remarksOrd, auxChar, c1, c2, c3, c4)
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
SELECT N.no                                AS numero,
       vendno                              AS fornecedor,
       CAST(NF.date AS DATE)               AS data,
       N.empno                             AS comprador,
       L.localizacao                       AS localizacao,
       X.obs                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.cost)       AS totalProdutos,
       MAX(X.auxShort4)                    AS marca,
       'N'                                 AS cancelada,
       CAST(IFNULL(NF.numero, '') AS CHAR) AS notaBaixa,
       NF.dataNota                         AS dataBaixa,
       N.s4                                AS singno,
       SU.name                             AS sing,
       TU.no                               AS transportadoNo,
       TU.name                             AS transportadoPor,
       RU.no                               AS recebidoNo,
       RU.name                             AS recebidoPor,
       PU.no                               AS usuarioNo,
       PU.name                             AS usuario
FROM sqldados.ords AS N
       LEFT JOIN T_PEDIDO_NOTA AS NF
                 ON N.no = NF.ordno
       INNER JOIN sqldados.oprd AS X
                  ON N.storeno = X.storeno AND N.no = X.ordno
       LEFT JOIN T_LOC AS L
                 ON X.prdno = L.prdno
                   AND X.grade = L.grade
       LEFT JOIN sqldados.users AS SU
                 ON N.s4 = SU.no
       LEFT JOIN sqldados.emp AS TU
                 ON N.s3 = TU.no
       LEFT JOIN sqldados.users AS RU
                 ON N.s2 = RU.no
       LEFT JOIN sqldados.users AS PU
                 ON N.padbyte = PU.no
WHERE N.date >= @DATA
  AND N.date >= 20240226
  AND (N.no LIKE CONCAT(:lojaRessu, '%') OR :lojaRessu = 0)
  AND (X.auxShort4 = :marca OR :marca = 999)
  AND (N.storeno = 1)
  AND (N.no = :ordno OR :ordno = 0)
  AND (L.localizacao IN (:locais) OR 'TODOS' IN (:locais))
  AND N.no >= 100000000
  AND N.date >= @DATA
GROUP BY N.storeno,
         N.no,
         IF(:marca = 999, '', SUBSTRING_INDEX(X.obs, '-', 1)),
         IF(:marca = 999, '', L.localizacao);

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_02;
CREATE TEMPORARY TABLE T_PEDIDO_02
SELECT N.no                                AS numero,
       vendno                              AS fornecedor,
       CAST(N.date AS DATE)                AS data,
       N.empno                             AS comprador,
       L.localizacao                       AS localizacao,
       X.obs                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.cost)       AS totalProdutos,
       MAX(X.auxShort4)                    AS marca,
       'N'                                 AS cancelada,
       CAST(IFNULL(NF.numero, '') AS CHAR) AS notaBaixa,
       NF.dataNota                         AS dataBaixa,
       SU.no                               AS singno,
       SU.name                             AS sing,
       TU.no                               AS transportadoNo,
       TU.name                             AS transportadoPor,
       RU.no                               AS recebidoNo,
       RU.name                             AS recebidoPor,
       PU.no                               AS usuarioNo,
       PU.name                             AS usuario
FROM sqldados.ordsRessu AS N
       INNER JOIN T_PEDIDO_NOTA AS NF
                  ON N.no = NF.ordno
       INNER JOIN sqldados.oprdRessu AS X
                  ON N.storeno = X.storeno AND N.no = X.ordno
       LEFT JOIN T_LOC AS L
                 ON X.prdno = L.prdno
                   AND X.grade = L.grade
       LEFT JOIN sqldados.users AS SU
                 ON N.s4 = SU.no
       LEFT JOIN sqldados.emp AS TU
                 ON N.s3 = TU.no
       LEFT JOIN sqldados.users AS RU
                 ON N.s2 = RU.no
       LEFT JOIN sqldados.users AS PU
                 ON N.padbyte = PU.no
WHERE N.date >= @DATA
  AND N.date >= 20240226
  AND (N.no LIKE CONCAT(:lojaRessu, '%') OR :lojaRessu = 0)
  AND (X.auxShort4 = :marca OR :marca = 999)
  AND (N.storeno = 1)
  AND (N.no = :ordno OR :ordno = 0)
  AND (L.localizacao IN (:locais) OR 'TODOS' IN (:locais))
  AND N.no >= 100000000
GROUP BY N.storeno,
         N.no,
         IF(:marca = 999, '', SUBSTRING_INDEX(X.obs, '-', 1)),
         IF(:marca = 999, '', L.localizacao);

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE TEMPORARY TABLE T_PEDIDO
SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       totalProdutos,
       marca,
       cancelada,
       notaBaixa,
       dataBaixa,
       singno,
       sing,
       transportadoNo,
       transportadoPor,
       recebidoNo,
       recebidoPor,
       usuarioNo,
       usuario
FROM T_PEDIDO_01
UNION
DISTINCT
SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       totalProdutos,
       marca,
       cancelada,
       notaBaixa,
       dataBaixa,
       singno,
       sing,
       transportadoNo,
       transportadoPor,
       recebidoNo,
       recebidoPor,
       usuarioNo,
       usuario
FROM T_PEDIDO_02;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       totalProdutos,
       marca,
       cancelada,
       MAX(notaBaixa)  AS notaBaixa,
       MAX(dataBaixa)  AS dataBaixa,
       singno          AS singno,
       sing            AS sing,
       transportadoNo  AS transportadoNo,
       transportadoPor AS transportadoPor,
       recebidoNo      AS recebidoNo,
       recebidoPor     AS recebidoPor,
       usuarioNo       AS usuarioNo,
       usuario         AS usuario
FROM T_PEDIDO AS D
WHERE (@PESQUISA = '' OR
       fornecedor LIKE @PESQUISA_NUM OR
       comprador LIKE @PESQUISA_NUM OR
       localizacao LIKE @PESQUISA OR
       singno LIKE @PESQUISA_NUM OR
       sing LIKE @PESQUISA_LIKE OR
       transportadoNo LIKE @PESQUISA_NUM OR
       transportadoPor LIKE @PESQUISA_LIKE OR
       recebidoNo LIKE @PESQUISA_NUM OR
       recebidoPor LIKE @PESQUISA_LIKE OR
       usuarioNo LIKE @PESQUISA_NUM OR
       usuario LIKE @PESQUISA_LIKE
        )
GROUP BY numero, localizacao
