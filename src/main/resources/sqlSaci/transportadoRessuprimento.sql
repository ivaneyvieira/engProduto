UPDATE sqldados.ords
SET s3 = :transportadoNo
WHERE storeno = :storeno
  AND no = :ordno;

UPDATE sqldados.ordsRessu
SET s3 = :transportadoNo
WHERE storeno = :storeno
  AND no = :ordno

