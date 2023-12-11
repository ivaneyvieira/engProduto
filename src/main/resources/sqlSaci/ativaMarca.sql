UPDATE sqldados.eord
SET eord.c3 = CONCAT(:marca, mid(eord.c3, length(:marca) + 1, 50))
WHERE ordno = :ordno
  AND storeno = :storeno