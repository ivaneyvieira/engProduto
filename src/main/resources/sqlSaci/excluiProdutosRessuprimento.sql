UPDATE sqldados.oprd AS O
SET O.auxShort4 = 999
WHERE O.storeno = :storeno
  AND O.ordno = :ordno
  AND O.prdno = :prdno
  AND O.grade = :grade;

UPDATE sqldados.oprdRessu AS O
SET O.auxShort4 = 999
WHERE O.storeno = :storeno
  AND O.ordno = :ordno
  AND O.prdno = :prdno
  AND O.grade = :grade
