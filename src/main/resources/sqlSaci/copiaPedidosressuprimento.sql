SELECT *
FROM
  sqldados.oprd
WHERE ordno = 500018597
  AND storeno = 1;

SELECT storeno, ordno, prdno, grade, seqno, auxShort4
FROM
  sqldados.oprdRessu
WHERE ordno = 500018597
  AND storeno = 1;

SELECT *
FROM
  sqldados.ords
WHERE no = 500018597
  AND storeno = 1;

SELECT *
FROM
  sqldados.ordsRessu
WHERE no = 500018597
  AND storeno = 1;

/*************************************************************************************/

INSERT INTO sqldados.ords(no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno, dataFaturamento,
                          invno, freightAmt, auxLong1, auxLong2, amtOrigem, dataEntrega, discountOrig, l1, l2, l3, l4,
                          m1, m2, m3, m4, deliv, storeno, carrno, empno, prazo, eord_storeno, delivOriginal, bits,
                          bits2, bits3, padbyte, indxno, repno, auxShort1, auxShort2, noofinst, status, s1, s2, s3, s4,
                          frete, remarks, ordnoFromVend, remarksInv, remarksRcv, remarksOrd, auxChar, c1, c2, c3, c4)
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
FROM
  sqldados.ordsRessu
WHERE no = 500018597
  AND storeno = 1;

INSERT INTO sqldados.oprd(ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2, icmsSubst, auxLong3,
                          auxLong4, auxMy3, auxMy4, qtty, qtty_src, qtty_xfr, cost, qttyRcv, qttyCancel, qttyVendaMes,
                          qttyVendaMesAnt, qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, storeno, seqno, status,
                          bits, bits2, auxShort1, auxShort2, auxShort3, auxShort4, prdno, grade, remarks, padbyte,
                          gradeFechada, obs, auxStr)
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
FROM
  sqldados.oprdRessu
WHERE ordno = 500018597
  AND storeno = 1;