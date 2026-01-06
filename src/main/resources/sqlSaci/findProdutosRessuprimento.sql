USE sqldados;

DO @DATA := SUBDATE(CURRENT_DATE, 30) * 1;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno AS prdno, A.grade AS grade, TRIM(A.localizacao) AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE ((TRIM(MID(A.localizacao, 1, 4)) IN (:local)) OR ('TODOS' IN (:local)) OR (A.localizacao = ''))
  AND (A.storeno = 4)
  AND (A.prdno = :prdno OR :prdno = '')
  AND (A.grade = :grade OR :grade = '');

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_NOTA;
CREATE TEMPORARY TABLE T_PEDIDO_NOTA
(
  PRIMARY KEY (storeno, numero, ordno, prdno, grade)
)
SELECT 1                           AS storeno,
       N.l2                        AS ordno,
       X.prdno                     AS prdno,
       X.grade                     AS grade,
       CONCAT(N.nfno, '/', N.nfse) AS numero,
       CAST(N.issuedate AS DATE)   AS dataNota,
       SUM(X.qtty / 1000)          AS qtty
FROM
  sqldados.nf                  AS N
    INNER JOIN sqldados.xaprd2 AS X
               USING (storeno, pdvno, xano)
WHERE N.l2 BETWEEN 100000000 AND 999999999
  AND N.issuedate >= @DATA
  AND N.issuedate >= 20240226
  AND N.status <> 1
  AND (X.prdno = :prdno OR :prdno = '')
  AND (X.grade = :grade OR :grade = '')
GROUP BY N.l2, numero, X.prdno, X.grade;

DROP TEMPORARY TABLE IF EXISTS T_ORDS;
CREATE TEMPORARY TABLE T_ORDS
(
  PRIMARY KEY (storeno, no)
)
SELECT *
FROM
  ( SELECT *
    FROM
      sqldados.ords
    WHERE storeno = 1
      AND no = :ordno
    UNION
    DISTINCT
    SELECT *
    FROM
      sqldados.ordsRessu
    WHERE storeno = 1
      AND no = :ordno ) AS D
GROUP BY storeno, no;

DROP TEMPORARY TABLE IF EXISTS T_OPRD;
CREATE TEMPORARY TABLE T_OPRD
(
  PRIMARY KEY (storeno, ordno, prdno, grade, seqno)
)
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
       auxStr,
       SUM(origem) AS origem
FROM
  ( SELECT O1.*, 1 AS origem
    FROM
      sqldados.oprd AS O1
    WHERE storeno = 1
      AND ordno = :ordno
      AND (prdno = :prdno OR :prdno = '')
      AND (grade = :grade OR :grade = '')
    UNION
    DISTINCT
    SELECT O2.*, 10 AS origem
    FROM
      sqldados.oprdRessu AS O2
    WHERE storeno = 1
      AND ordno = :ordno
      AND (prdno = :prdno OR :prdno = '')
      AND (grade = :grade OR :grade = '')
      AND (:ressu = 'S') ) AS D
GROUP BY storeno, ordno, prdno, grade, seqno;

UPDATE sqldados.oprd AS O INNER JOIN T_OPRD AS T USING (storeno, ordno, prdno, grade, seqno)
SET O.auxMy2 = T.qtty,
    T.auxMy2 = T.qtty
WHERE O.auxMy2 = 0;

UPDATE sqldados.oprdRessu AS O INNER JOIN T_OPRD AS T USING (storeno, ordno, prdno, grade, seqno)
SET O.auxMy2 = T.qtty,
    T.auxMy2 = T.qtty
WHERE O.auxMy2 = 0;

DROP TEMPORARY TABLE IF EXISTS T_VENC;
CREATE TEMPORARY TABLE T_VENC
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, GROUP_CONCAT(mesAno) AS vencimentoStrList
FROM
  sqldados.produtoEntrada
WHERE mesAno > 0
  AND loja = 4
  AND date >= 20240501
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ORDS_RESULT;
CREATE TEMPORARY TABLE T_ORDS_RESULT
SELECT X.ordno                                                     AS ordno,
       CAST(TRIM(P.no) AS CHAR)                                    AS codigo,
       P.no                                                        AS prdno,
       IFNULL(X.grade, '')                                         AS grade,
       IF(IFNULL(X.grade, '') = '',
          CONCAT(TRIM(P.barcode), ',', GROUP_CONCAT(TRIM(IFNULL(B.barcode, '')) SEPARATOR ',')),
          GROUP_CONCAT(TRIM(IFNULL(B.barcode, '')) SEPARATOR ',')) AS barcodeListStr,
       TRIM(MID(P.name, 1, 37))                                    AS descricao,
       P.mfno                                                      AS vendno,
       IFNULL(F.auxChar1, '')                                      AS fornecedor,
       P.mfno_ref                                                  AS vendnoRef,
       P.typeno                                                    AS typeno,
       IFNULL(T.name, '')                                          AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                          AS clno,
       IFNULL(cl.name, '')                                         AS clname,
       P.m6                                                        AS altura,
       P.m4                                                        AS comprimento,
       P.m5                                                        AS largura,
       P.sp / 100                                                  AS precoCheio,
       X.qtty                                                      AS qtPedido,
       X.auxMy2                                                    AS qttyOriginal,
       IFNULL(TN.qtty, X.qtty)                                     AS qtQuantNF,
       X.qtty * S.cm_real / 10000                                  AS vlPedido,
       TN.qtty * S.cm_real / 10000                                 AS vlQuantNF,
       X.auxLong2                                                  AS qtEntregue,
       X.auxLong1                                                  AS qtRecebido,
       X.auxMy4                                                    AS qtAvaria,
       X.auxMy3                                                    AS qtVencido,
  /* IF(X.auxLong1 = 99999, TN.qtty, X.auxLong1) * S.cm_real / 10000 AS vlRecebido, */
       X.cost                                                      AS preco,
       (X.qtty * X.mult / 1000) * X.cost                           AS total,
       X.auxShort4                                                 AS marca,
       X.auxShort3                                                 AS selecionado,
       X.auxLong4                                                  AS posicao,
       IFNULL(L.localizacao, '')                                   AS localizacao,
       ROUND(IFNULL(S.qtty_varejo + S.qtty_atacado, 0) / 1000)     AS estoque,
       SUBSTRING_INDEX(X.obs, ':', 1)                              AS codigoCorrecao,
       TRIM(MID(PR.name, 1, 37))                                   AS descricaoCorrecao,
       SUBSTRING_INDEX(SUBSTRING_INDEX(X.obs, ':', 2), ':', -1)    AS gradeCorrecao,
       TN.numero                                                   AS numeroNota,
       TN.dataNota                                                 AS dataNota,
       IF(origem IN (1, 11), 'S', 'N')                             AS origemSaci,
       IF(origem IN (10, 11), 'S', 'N')                            AS origemApp,
       IF(P.tipoGarantia = 2, P.garantia, 0)                       AS validade,
       IFNULL(V.vencimentoStrList, '')                             AS vencimentoStrList
FROM
  T_OPRD                       AS X
    LEFT JOIN  T_VENC          AS V
               USING (prdno, grade)
    INNER JOIN T_ORDS          AS N
               ON N.storeno = X.storeno AND N.no = X.ordno
    INNER JOIN sqldados.prd    AS P
               ON P.no = X.prdno
    LEFT JOIN  T_PEDIDO_NOTA   AS TN
               ON TN.storeno = X.storeno AND TN.ordno = X.ordno AND TN.prdno = X.prdno AND TN.grade = X.grade
    LEFT JOIN  sqldados.stk    AS S
               ON S.prdno = X.prdno AND S.grade = X.grade AND S.storeno = 4
    LEFT JOIN  sqldados.prdbar AS B
               ON B.prdno = P.no AND B.grade = X.grade
    LEFT JOIN  T_LOC           AS L
               ON X.prdno = L.prdno AND X.grade = L.grade
    LEFT JOIN  sqldados.vend   AS F
               ON F.no = P.mfno
    LEFT JOIN  sqldados.type   AS T
               ON T.no = P.typeno
    LEFT JOIN  sqldados.cl
               ON cl.no = P.clno
    LEFT JOIN  sqldados.prd    AS PR
               ON PR.no = LPAD(SUBSTRING_INDEX(X.obs, ':', 1), 16, ' ')
WHERE X.storeno = 1
  AND X.ordno = :ordno
  AND (X.auxShort4 = :marca OR :marca = 999)
GROUP BY codigo, IFNULL(X.grade, ''), numeroNota;

DROP TEMPORARY TABLE IF EXISTS T_PRD_GRADE;
CREATE TEMPORARY TABLE T_PRD_GRADE
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade
FROM
  T_ORDS_RESULT
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_PRD_GRADE_INV;
CREATE TEMPORARY TABLE T_PRD_GRADE_INV
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, MAX(invno) AS invno
FROM
  sqldados.inv               AS N
    INNER JOIN sqldados.iprd AS X
               USING (invno)
    INNER JOIN T_PRD_GRADE
               USING (prdno, grade)
WHERE N.type = 0
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_PRD_INV;
CREATE TEMPORARY TABLE T_PRD_INV
(
  PRIMARY KEY (prdno)
)
SELECT prdno, MAX(invno) AS invno
FROM
  sqldados.inv               AS N
    INNER JOIN sqldados.iprd AS X
               USING (invno)
    INNER JOIN T_PRD_GRADE
               USING (prdno, grade)
WHERE N.type = 0
GROUP BY prdno;

DROP TEMPORARY TABLE IF EXISTS T_PRD_GRADE_VALOR;
CREATE TEMPORARY TABLE T_PRD_GRADE_VALOR
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, X.dfob AS valor
FROM
  sqldados.iprd                AS X
    INNER JOIN T_PRD_GRADE_INV AS P
               USING (invno, prdno, grade)
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_PRD_VALOR;
CREATE TEMPORARY TABLE T_PRD_VALOR
(
  PRIMARY KEY (prdno)
)
SELECT prdno, X.dfob AS valor
FROM
  sqldados.iprd          AS X
    INNER JOIN T_PRD_INV AS P
               USING (invno, prdno)
GROUP BY prdno;

SELECT ordno,
       codigo,
       prdno,
       grade,
       barcodeListStr,
       descricao,
       vendno,
       fornecedor,
       vendnoRef,
       typeno,
       typeName,
       clno,
       clname,
       altura,
       comprimento,
       largura,
       precoCheio,
       qtPedido,
       qttyOriginal,
       qtQuantNF,
       vlPedido,
       vlQuantNF,
       qtEntregue,
       qtRecebido,
       qtAvaria,
       qtVencido,
       preco,
       total,
       marca,
       selecionado,
       posicao,
       localizacao,
       estoque,
       codigoCorrecao,
       descricaoCorrecao,
       gradeCorrecao,
       numeroNota,
       dataNota,
       origemSaci,
       origemApp,
       validade,
       vencimentoStrList,
       ROUND(COALESCE(G.valor, P.valor), 2)            AS valorUltCompra,
       qtPedido * ROUND(COALESCE(G.valor, P.valor), 2) AS valorTotal
FROM
  T_ORDS_RESULT                 AS R
    LEFT JOIN T_PRD_GRADE_VALOR AS G
              USING (prdno, grade)
    LEFT JOIN T_PRD_VALOR       AS P
              USING (prdno)