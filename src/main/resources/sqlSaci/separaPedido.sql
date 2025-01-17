USE sqldados;

DO @ORDNO_NOVO := :ordnoNovo;

INSERT IGNORE INTO
    ordsPendente(no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno,
                 dataFaturamento, invno, freightAmt, auxLong1, auxLong2, amtOrigem, dataEntrega,
                 discountOrig, l1, l2, l3, l4, m1, m2, m3, m4, deliv, storeno, carrno, empno, prazo,
                 eord_storeno, delivOriginal, bits, bits2, bits3, padbyte, indxno, repno, auxShort1,
                 auxShort2, noofinst, status, s1, s2, s3, s4, frete, remarks, ordnoFromVend, remarksInv,
                 remarksRcv, remarksOrd, auxChar, c1, c2, c3, c4)
SELECT
    no,
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
FROM ords o
WHERE
      storeno = 1
  AND no = :ordno;

INSERT IGNORE INTO
    oprdPendente(date, ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1,
                 auxMy2, icmsSubst, auxLong3, auxLong4, auxMy3, auxMy4, qtty,
                 qtty_src, qtty_xfr, cost, qttyRcv, qttyCancel, qttyVendaMes,
                 qttyVendaMesAnt, qttyVendaMedia, qttyPendente, stkDisponivel,
                 qttyAbc, storeno, seqno, status, bits, bits2, auxShort1, auxShort2,
                 auxShort3, auxShort4, prdno, grade, remarks, padbyte, gradeFechada,
                 obs, auxStr)
SELECT
    O.date,
    ordno,
    mult,
    ipi,
    freight,
    icms,
    P.auxLong1,
    P.auxLong2,
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
    P.storeno,
    seqno,
    P.status,
    P.bits,
    P.bits2,
    P.auxShort1,
    P.auxShort2,
    auxShort3,
    auxShort4,
    prdno,
    grade,
    P.remarks,
    P.padbyte,
    gradeFechada,
    obs,
    auxStr
FROM ords AS O INNER JOIN oprd AS P
                              ON O.no = P.ordno AND P.storeno = O.storeno
WHERE
      O.storeno = 1
  AND O.no = :ordno;

INSERT IGNORE INTO
    ords (no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno,
          dataFaturamento, invno, freightAmt, auxLong1, auxLong2, amtOrigem,
          dataEntrega, discountOrig, l1, l2, l3, l4, m1, m2, m3, m4, deliv, storeno,
          carrno, empno, prazo, eord_storeno, delivOriginal, bits, bits2, bits3,
          padbyte, indxno, repno, auxShort1, auxShort2, noofinst, status, s1, s2, s3,
          s4, frete, remarks, ordnoFromVend, remarksInv, remarksRcv, remarksOrd,
          auxChar, c1, c2, c3, c4)
SELECT
    @ORDNO_NOVO      AS no,
    CURRENT_DATE * 1 AS date,
    vendno,
    discount,
    0                AS amt,
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
    :ordno           AS l2,
    l3,
    l4,
    m1,
    m2,
    m3,
    m4,
    deliv,
    1                AS storeno,
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
    0,
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
    'S'              AS c4
FROM ords
WHERE
      storeno = 1
  AND NO = :ordno;

INSERT INTO
    oprd (storeno, ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2,
          icmsSubst, auxLong3, auxLong4, auxMy3, auxMy4, qtty, qtty_src, qtty_xfr, cost,
          qttyRcv, qttyCancel, qttyVendaMes, qttyVendaMesAnt, qttyVendaMedia, qttyPendente,
          stkDisponivel, qttyAbc, seqno, status, bits, bits2, auxShort1, auxShort2,
          auxShort3, auxShort4, prdno, grade, remarks, padbyte, gradeFechada, obs, auxStr)
SELECT
    1                   AS storeno,
    @ORDNO_NOVO         AS ordno,
    mult,
    ipi,
    freight,
    icms,
    auxLong1,
    auxLong2,
    auxMy1,
    auxMy2,
    icmsSubst,
    ROUND(:qtty * 1000) AS auxLong3,
    auxLong4,
    auxMy3,
    auxMy4,
    :qtty               AS qtty,
    :qtty               AS qtty_src,
    qtty_xfr,
    cost,
    0                   AS qttyRcv,
    0                   AS qttyCancel,
    qttyVendaMes,
    qttyVendaMesAnt,
    qttyVendaMedia,
    qttyPendente,
    stkDisponivel,
    qttyAbc,
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
    :localizacao        AS auxStr
FROM oprd
WHERE
      (storeno = 1)
  AND (ordno = :ordno)
  AND (prdno = :prdno)
  AND (grade = :grade)
GROUP BY
    storeno, ordno, prdno, grade;

/********************************************************************/

UPDATE oprd
SET
    qtty = (qtty - :qtty)
WHERE
      (storeno = 1)
  AND (ordno = :ordno)
  AND (prdno = :prdno)
  AND (grade = :grade);

UPDATE oprdRessu
SET
    qtty = (qtty - :qtty)
WHERE
      (storeno = 1)
  AND (ordno = :ordno)
  AND (prdno = :prdno)
  AND (grade = :grade);

/********************************************************************/

DELETE
FROM oprd
WHERE
      storeno = 1
  AND ordno = :ordno
  AND prdno = :prdno
  AND grade = :grade
  AND ROUND(qtty) <= 0;

DELETE
FROM oprd
WHERE
      storeno = 1
  AND ordno = @ORDNO_NOVO
  AND prdno = :prdno
  AND grade = :grade
  AND ROUND(qtty) <= 0;

DELETE
FROM oprdRessu
WHERE
      storeno = 1
  AND ordno = :ordno
  AND prdno = :prdno
  AND grade = :grade
  AND ROUND(qtty) <= 0;

DELETE
FROM oprdRessu
WHERE
      storeno = 1
  AND ordno = @ORDNO_NOVO
  AND prdno = :prdno
  AND grade = :grade
  AND ROUND(qtty) <= 0;

/********************************************************************/

INSERT IGNORE INTO
    sqldados.lastno(storeno, no, dupse, se, padbyte) VALUE (MID(@ORDNO_NOVO, 1, 1) * 1, @ORDNO_NOVO, 0, 'RS', '');

SELECT @ORDNO_NOVO AS ordno