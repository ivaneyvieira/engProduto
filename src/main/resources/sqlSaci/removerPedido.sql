USE sqldados;

DELETE
FROM ords
WHERE storeno = 1
  AND no = :ordno;

DELETE
FROM ordsRessu
WHERE storeno = 1
  AND no = :ordno;

DELETE
FROM oprdRessu
WHERE storeno = 1
  AND ordno = :ordno;

DELETE
FROM orddlv
WHERE storeno = 1
  AND ordno = :ordno;

DELETE
FROM oprd
WHERE storeno = 1
  AND ordno = :ordno;

DELETE
FROM oprdxf
WHERE storeno = 1
  AND ordno = :ordno