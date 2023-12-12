UPDATE sqldados.eord
SET eord.l9 = IF(:data IS NULL, 0, :data),
    eord.l8 = IF(:hora IS NULL, 0, TIME_TO_SEC(:hora)),
    eord.l5 = :userno
WHERE ordno = :ordno
  AND storeno = :storeno
