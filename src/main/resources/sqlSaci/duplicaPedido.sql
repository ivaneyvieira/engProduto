DO @ORDNO_FIM := (MID(:ordno, 1, 1) + 1) * (RPAD('1', LENGTH(:ordno), '0') * 1);

DO @ORDNO := ( SELECT MAX(no + 1) AS no
               FROM
                 sqldados.ords
               WHERE
                   storeno = 1
               AND no >= :ordno
               AND no < @ORDNO_FIM );

INSERT INTO sqldados.ords (no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno, dataFaturamento,
                           invno, freightAmt, auxLong1, auxLong2, amtOrigem, dataEntrega, discountOrig, l1, l2, l3, l4,
                           m1, m2, m3, m4, deliv, storeno, carrno, empno, prazo, eord_storeno, delivOriginal, bits,
                           bits2, bits3, padbyte, indxno, repno, auxShort1, auxShort2, noofinst, status, s1, s2, s3, s4,
                           frete, remarks, ordnoFromVend, remarksInv, remarksRcv, remarksOrd, auxChar, c1, c2, c3, c4)
SELECT @ORDNO  AS no,
       date,
       vendno,
       discount,
       0       AS amt,
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
       :ordno  AS l2,
       l3,
       l4,
       m1,
       m2,
       m3,
       m4,
       deliv,
       storeno AS storeno,
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
       c4
FROM
  sqldados.ords
WHERE
    storeno = 1
AND no = :ordno;

INSERT INTO sqldados.oprd (storeno, ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2, icmsSubst,
                           auxLong3, auxLong4, auxMy3, auxMy4, qtty, qtty_src, qtty_xfr, cost, qttyRcv, qttyCancel,
                           qttyVendaMes, qttyVendaMesAnt, qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, seqno,
                           status, bits, bits2, auxShort1, auxShort2, auxShort3, auxShort4, prdno, grade, remarks,
                           padbyte, gradeFechada, obs, auxStr)
SELECT storeno            AS storeno,
       @ORDNO             AS ordno,
       mult,
       ipi,
       freight,
       icms,
       auxLong1,
       auxLong2,
       auxMy1,
       auxMy2,
       icmsSubst,
       ROUND(qtty * 1000) AS auxLong3,
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
       'D'                AS auxStr
FROM
  sqldados.oprd
WHERE
    (storeno = 1)
AND (ordno = :ordno)
GROUP BY
  prdno, grade;

SELECT @ORDNO AS ordno
