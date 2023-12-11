UPDATE sqldados.eord
SET eord.l9 = IF(:data IS NULL, 0, :data),
    eord.l8 = IF(:hora IS NULL, 0, TIME_TO_SEC(:hora))
WHERE ordno = :ordno
  AND storeno = :storeno
/**
l13 -> l8
l14 -> l9

 */