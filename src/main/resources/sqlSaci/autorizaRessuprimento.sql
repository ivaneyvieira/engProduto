use
sqldados;

UPDATE sqldados.ords
SET s4 = :userno
WHERE storeno = :storeno
  AND no = :ordno;

UPDATE sqldados.ordsRessu
SET s4 = :userno
WHERE storeno = :storeno
  AND no = :ordno
