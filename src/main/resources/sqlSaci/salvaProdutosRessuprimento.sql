UPDATE sqldados.oprd AS X
SET X.auxShort4 = :marca,
    X.obs       = :usuarioCD
WHERE storeno = 1
  AND ordno = :ordno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade;

REPLACE INTO sqldados.oprdRessu(ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2,
				icmsSubst, auxLong3, auxLong4, auxMy3, auxMy4, qtty, qtty_src,
				qtty_xfr, cost, qttyRcv, qttyCancel, qttyVendaMes, qttyVendaMesAnt,
				qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, storeno,
				seqno, status, bits, bits2, auxShort1, auxShort2, auxShort3,
				auxShort4, prdno, grade, remarks, padbyte, gradeFechada, obs,
				auxStr)
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
  AND ordno = :ordno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade
