UPDATE sqldados.oprd
SET qtty = :qtty
WHERE storeno = 1
  AND ordno = :ordno
  AND prdno = :prdno
  AND grade = :grade;

UPDATE sqldados.oprdRessu
SET qtty = :qtty
WHERE storeno = 1
  AND ordno = :ordno
  AND prdno = :prdno
  AND grade = :grade