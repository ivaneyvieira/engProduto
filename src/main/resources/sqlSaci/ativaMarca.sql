UPDATE sqldados.eord
SET eord.c3 = CONCAT(:marca, MID(eord.c3, LENGTH(:marca) + 1, 50))
WHERE ordno = :ordno
  AND storeno = :storeno