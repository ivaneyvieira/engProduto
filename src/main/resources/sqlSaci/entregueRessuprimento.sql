UPDATE sqldados.ords
SET s4 = :singno
WHERE storeno = :storeno
  AND no = :ordno;

UPDATE sqldados.ordsRessu
SET s4 = :singno
WHERE storeno = :storeno
  AND no = :ordno

