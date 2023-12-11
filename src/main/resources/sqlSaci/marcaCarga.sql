UPDATE sqldados.eord
SET eord.c3 = RTRIM(CONCAT(RPAD(MID(eord.c3, 1, 3), 3, ' '), RPAD(:marca, 1, ' '),
                           LPAD(:entrega, 8, '0'), MID(eord.c3, 13, 50)))
WHERE ordno = :ordno
  AND storeno = :storeno