UPDATE sqldados.eord
SET eord.c3 = RTRIM(CONCAT(RPAD(MID(eord.c3, 1, 2), 2, ' '), RPAD(:marca, 1, ' '), MID(eord.c3, 4, 50)))
WHERE ordno = :ordno
  AND storeno = :storeno