USE sqldados;

DO @DATA := SUBDATE(CURRENT_DATE, 30) * 1;

/*
CREATE TABLE sqldados.ordsAdicional
(
  storeno     INT,
  ordno       INT,
  localizacao VARCHAR(4),
  observacao varchar(100),
  PRIMARY KEY (storeno, ordno, localizacao)
)
*/

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno                  AS prdno,
       A.grade                  AS grade,
       MID(A.localizacao, 1, 4) AS localizacao
FROM sqldados.prdAdicional AS A
WHERE A.storeno = 4
  AND A.localizacao != ''
  AND (A.prdno = LPAD(:codigo, 16, ' ') OR :codigo = '')
  AND (A.grade = :grade OR :grade = '')
  AND (MID(A.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY A.prdno, A.grade;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_NOTA;
CREATE TEMPORARY TABLE T_PEDIDO_NOTA
(
  PRIMARY KEY (storeno, ordno, numero, prdno, grade)
)
SELECT 1                           AS storeno,
       N.l2                        AS ordno,
       X.prdno                     AS prdno,
       X.grade                     AS grade,
       N.storeno                   AS lojaNF,
       CONCAT(N.nfno, '/', N.nfse) AS numero,
       CAST(N.issuedate AS DATE)   AS dataNota,
       SUM(N.grossamt / 100)       AS valorNota,
       SUM(X.qtty / 1000)          AS qtty
FROM sqldados.nf AS N
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
WHERE N.l2 BETWEEN 100000000 AND 999999999
  AND N.issuedate >= @DATA
  AND N.issuedate >= 20240307
  AND N.status <> 1
  AND ((X.prdno = LPAD(:codigo, 16, ' ')) OR :codigo = '')
  AND ((X.grade = :grade) OR :grade = '')
GROUP BY storeno, ordno, numero, prdno, grade;

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
       CAST(N.date AS DATE)                AS data,
       N.empno                             AS comprador,
       L.localizacao                       AS localizacao,
       X.obs                               AS usuarioCD,
       NF.lojaNF                           AS loja,
       CAST(IFNULL(NF.numero, '') AS CHAR) AS notaBaixa,
       NF.dataNota                         AS dataBaixa,
       NF.valorNota                        AS valorNota,
       N.s4                                AS entregueNo,
       SU.name                             AS entreguePor,
       SU.login                            AS entregueSPor,
       TU.no                               AS transportadoNo,
       TU.name                             AS transportadoPor,
       TRIM(MID(TU.sname, 1, 15))          AS transportadoSPor,
       RU.no                               AS recebidoNo,
       RU.name                             AS recebidoPor,
       RU.login                            AS recebidoSPor,
       DU.no                               AS devolvidoNo,
       DU.name                             AS devolvidoPor,
       DU.login                            AS devolvidoSPor,
       PU.no                               AS usuarioNo,
       PU.name                             AS usuario,
       PU.login                            AS login,
       SUM(X.auxShort4 = 0)                AS countCD,
       SUM(X.auxShort4 = 1)                AS countENT,
       SUM(X.auxShort4 = 2)                AS countREC,
       SUM(SUBSTRING_INDEX(X.obs, ':', 1) != '' OR
           SUBSTRING_INDEX(SUBSTRING_INDEX(X.obs, ':', 2), ':', -1) != '' OR
           X.auxLong1 != NF.qtty OR
           X.auxLong2 != 0)                AS countCor,
       SUM(NF.ordno IS NOT NULL)           AS countNot,
       SUM(X.auxShort4 = 0
         AND X.auxShort3 != 0)             AS countSelCD,
       SUM(X.auxShort4 = 1
         AND X.auxShort3 != 0)             AS countSelENT,
       SUM(X.auxShort4 = 2
         AND X.auxShort3 != 0)             AS countSelREC
FROM sqldados.ords AS N
       INNER JOIN sqldados.oprd AS X
                  ON N.storeno = X.storeno
                    AND N.no = X.ordno
       LEFT JOIN T_PEDIDO_NOTA AS NF
                 ON X.ordno = NF.ordno
                   AND X.storeno = NF.storeno
                   AND X.prdno = NF.prdno
                   AND X.grade = NF.grade
       LEFT JOIN T_LOC AS L
                 ON X.prdno = L.prdno
                   AND X.grade = L.grade
       LEFT JOIN sqldados.users AS SU
                 ON N.s4 = SU.no
       LEFT JOIN sqldados.emp AS TU
                 ON N.s3 = TU.no
       LEFT JOIN sqldados.users AS RU
                 ON N.s2 = RU.no
       LEFT JOIN sqldados.users AS DU
                 ON N.s1 = DU.no
       LEFT JOIN sqldados.users AS PU
                 ON N.padbyte = PU.no
WHERE N.date >= @DATA
  AND N.date >= 20240307
  AND (N.no LIKE CONCAT(:lojaRessu, '%') OR :lojaRessu = 0)
  AND (N.storeno = 1)
  AND (N.no = :ordno OR :ordno = 0)
  AND N.no >= 100000000
  AND N.date >= @DATA
  AND ((X.prdno = LPAD(:codigo, 16, ' ') AND X.auxShort4 = :marca) OR :codigo = '')
  AND ((X.grade = :grade AND X.auxShort4 = :marca) OR :grade = '')
GROUP BY N.storeno,
         N.no,
         L.localizacao,
         notaBaixa;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_02;
CREATE TEMPORARY TABLE T_PEDIDO_02
SELECT N.no                                AS numero,
       vendno                              AS fornecedor,
       CAST(N.date AS DATE)                AS data,
       N.empno                             AS comprador,
       L.localizacao                       AS localizacao,
       X.obs                               AS usuarioCD,
       NF.lojaNF                           AS loja,
       CAST(IFNULL(NF.numero, '') AS CHAR) AS notaBaixa,
       NF.dataNota                         AS dataBaixa,
       NF.valorNota                        AS valorNota,
       N.s4                                AS entregueNo,
       SU.name                             AS entreguePor,
       SU.login                            AS entregueSPor,
       TU.no                               AS transportadoNo,
       TU.name                             AS transportadoPor,
       TRIM(MID(TU.sname, 1, 15))          AS transportadoSPor,
       RU.no                               AS recebidoNo,
       RU.name                             AS recebidoPor,
       RU.login                            AS recebidoSPor,
       DU.no                               AS devolvidoNo,
       DU.name                             AS devolvidoPor,
       DU.login                            AS devolvidoSPor,
       PU.no                               AS usuarioNo,
       PU.name                             AS usuario,
       PU.login                            AS login,
       SUM(X.auxShort4 = 0)                AS countCD,
       SUM(X.auxShort4 = 1)                AS countENT,
       SUM(X.auxShort4 = 2)                AS countREC,
       SUM(SUBSTRING_INDEX(X.obs, ':', 1) != '' OR
           SUBSTRING_INDEX(SUBSTRING_INDEX(X.obs, ':', 2), ':', -1) != '' OR
           X.auxLong1 != NF.qtty OR
           X.auxLong2 != 0)                AS countCor,
       SUM(NF.ordno IS NOT NULL)           AS countNot,
       SUM(X.auxShort4 = 0
         AND X.auxShort3 != 0)             AS countSelCD,
       SUM(X.auxShort4 = 1
         AND X.auxShort3 != 0)             AS countSelENT,
       SUM(X.auxShort4 = 2
         AND X.auxShort3 != 0)             AS countSelREC
FROM sqldados.ordsRessu AS N
       INNER JOIN sqldados.oprdRessu AS X
                  ON N.storeno = X.storeno
                    AND N.no = X.ordno
       LEFT JOIN T_PEDIDO_NOTA AS NF
                 ON X.ordno = NF.ordno
                   AND X.storeno = NF.storeno
                   AND X.prdno = NF.prdno
                   AND X.grade = NF.grade
       LEFT JOIN T_LOC AS L
                 ON X.prdno = L.prdno
                   AND X.grade = L.grade
       LEFT JOIN sqldados.users AS SU
                 ON N.s4 = SU.no
       LEFT JOIN sqldados.emp AS TU
                 ON N.s3 = TU.no
       LEFT JOIN sqldados.users AS RU
                 ON N.s2 = RU.no
       LEFT JOIN sqldados.users AS DU
                 ON N.s1 = DU.no
       LEFT JOIN sqldados.users AS PU
                 ON N.padbyte = PU.no
WHERE N.date >= @DATA
  AND N.date >= 20240307
  AND (N.no LIKE CONCAT(:lojaRessu, '%') OR :lojaRessu = 0)
  AND (N.storeno = 1)
  AND (N.no = :ordno OR :ordno = 0)
  AND N.no >= 100000000
  AND ((X.prdno = LPAD(:codigo, 16, ' ') AND X.auxShort4 = :marca) OR :codigo = '')
  AND ((X.grade = :grade AND X.auxShort4 = :marca) OR :grade = '')
GROUP BY N.storeno,
         N.no,
         L.localizacao,
         notaBaixa;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE TEMPORARY TABLE T_PEDIDO
SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       loja      AS loja,
       notaBaixa AS notaBaixaPedido,
       dataBaixa AS dataBaixaPedido,
       valorNota,
       entregueNo,
       entreguePor,
       entregueSPor,
       transportadoNo,
       transportadoPor,
       transportadoSPor,
       recebidoNo,
       recebidoPor,
       recebidoSPor,
       devolvidoNo,
       devolvidoPor,
       devolvidoSPor,
       usuarioNo,
       usuario,
       login,
       countCD,
       countENT,
       countREC,
       countCor,
       countNot,
       countSelCD,
       countSelENT,
       countSelREC
FROM T_PEDIDO_01
UNION
DISTINCT
SELECT numero,
       fornecedor,
       data,
       comprador,
       localizacao,
       loja,
       notaBaixa AS notaBaixaPedido,
       dataBaixa AS dataBaixaPedido,
       valorNota,
       entregueNo,
       entreguePor,
       entregueSPor,
       transportadoNo,
       transportadoPor,
       transportadoSPor,
       recebidoNo,
       recebidoPor,
       recebidoSPor,
       devolvidoNo,
       devolvidoPor,
       devolvidoSPor,
       usuarioNo,
       usuario,
       login,
       countCD,
       countENT,
       countREC,
       countCor,
       countNot,
       countSelCD,
       countSelENT,
       countSelREC
FROM T_PEDIDO_02;


DROP TEMPORARY TABLE IF EXISTS T_OBS;
CREATE TEMPORARY TABLE T_OBS
(
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno, ordno, observacao
FROM sqldados.ordsAdicional
GROUP BY storeno, ordno;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

SELECT numero,
       fornecedor,
       data,
       comprador,
       GROUP_CONCAT(DISTINCT
                    IF(D.localizacao = '', NULL, D.localizacao)
                    SEPARATOR ',')   AS localizacoes,
       :marca                        AS marca,
       :temNota                      AS temNota,
       loja                          AS loja,
       GROUP_CONCAT(DISTINCT
                    IF(notaBaixaPedido = '', NULL, notabaixaPedido)
                    SEPARATOR ',')   AS notaBaixa,
       MAX(dataBaixaPedido)          AS dataBaixa,
       valorNota                     AS valorNota,
       entregueNo                    AS entregueNo,
       entreguePor                   AS entreguePor,
       entregueSPor                  AS entregueSPor,
       transportadoNo                AS transportadoNo,
       transportadoPor               AS transportadoPor,
       transportadoSPor              AS transportadoSPor,
       recebidoNo                    AS recebidoNo,
       recebidoPor                   AS recebidoPor,
       recebidoSPor                  AS recebidoSPor,
       devolvidoNo                   AS devolvidoNo,
       devolvidoPor                  AS devolvidoPor,
       devolvidoSPor                 AS devolvidoSPor,
       usuarioNo                     AS usuarioNo,
       usuario                       AS usuario,
       login                         AS login,
       MAX(IFNULL(A.observacao, '')) AS observacao,
       SUM(countCD)                  AS countCD,
       SUM(countENT)                 AS countENT,
       SUM(countREC)                 AS countREC,
       SUM(countCor)                 AS countCor,
       SUM(countNot)                 AS countNot,
       SUM(countSelCD)               AS countSelCD,
       SUM(countSelENT)              AS countSelENT,
       SUM(countSelREC)              AS countSelREC
FROM T_PEDIDO AS D
       LEFT JOIN T_OBS AS A
                 ON A.storeno = 1
                   AND A.ordno = D.numero
WHERE (@PESQUISA = '' OR
       numero LIKE @PESQUISA_START OR
       fornecedor LIKE @PESQUISA_NUM OR
       comprador LIKE @PESQUISA_NUM OR
       D.localizacao LIKE @PESQUISA OR
       entregueNo LIKE @PESQUISA_NUM OR
       entreguePor LIKE @PESQUISA_LIKE OR
       entregueSPor LIKE @PESQUISA_LIKE OR
       notaBaixaPedido LIKE @PESQUISA_START OR
       transportadoNo LIKE @PESQUISA_NUM OR
       transportadoPor LIKE @PESQUISA_LIKE OR
       transportadoSPor LIKE @PESQUISA_LIKE OR
       recebidoNo LIKE @PESQUISA_NUM OR
       recebidoPor LIKE @PESQUISA_LIKE OR
       recebidoSPor LIKE @PESQUISA_LIKE OR
       usuarioNo LIKE @PESQUISA_NUM OR
       usuario LIKE @PESQUISA_LIKE
  )
  AND (data >= :dataPedidoInicial OR :dataPedidoInicial = 0)
  AND (data <= :dataPedidoFinal OR :dataPedidoFinal = 0)
  AND (dataBaixaPedido >= :dataNotaInicial OR :dataNotaInicial = 0 OR dataBaixaPedido IS NULL)
  AND (dataBaixaPedido <= :dataNotaFinal OR :dataNotaFinal = 0 OR dataBaixaPedido IS NULL)
GROUP BY D.numero
HAVING CASE
         WHEN :marca = 0 THEN SUM(countCD) > 0
         WHEN :marca = 1 AND :temNota = 'T' THEN SUM(countENT) > 0
         WHEN :marca = 1 AND :temNota = 'N' THEN SUM(countEnt) > 0 AND IFNULL(notaBaixa, '') = ''
         WHEN :marca = 1 AND :temNota = 'S' THEN SUM(countEnt) > 0 AND IFNULL(notaBaixa, '') != ''
         WHEN :marca = 2 THEN SUM(countRec) > 0 AND notaBaixa != ''
         ELSE FALSE
       END

