DELETE
FROM sqldados.oprd
WHERE storeno = 1
  AND ordno = :ordno
  AND prdno = :prdno
  AND grade = :grade;

DELETE
FROM sqldados.oprdRessu
WHERE storeno = 1
  AND ordno = :ordno
  AND prdno = :prdno
  AND grade = :grade

