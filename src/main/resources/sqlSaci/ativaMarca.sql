UPDATE sqldados.eord
SET eord.c3 = CONCAT(:marca, MID(eord.c3, LENGTH(:marca) + 1, 50))
WHERE ordno = :ordno
  AND storeno = :storeno;

UPDATE sqldados.nfr
SET c2 = CONCAT(:marca, MID(c2, LENGTH(:marca) + 1, 50))
WHERE auxLong1 = :ordno
  AND storeno = :storeno;
