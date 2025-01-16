UPDATE sqldados.oprd AS O
SET O.auxShort4 = 999
WHERE O.storeno = :storeno
  AND O.ordno = :ordno
  AND O.auxShort4 = :marca;

UPDATE sqldados.oprdRessu AS O
SET O.auxShort4 = 999
WHERE O.storeno = :storeno
  AND O.ordno = :ordno
  AND O.auxShort4 = :marca

