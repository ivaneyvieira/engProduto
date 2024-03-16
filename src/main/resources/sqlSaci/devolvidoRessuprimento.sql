UPDATE sqldados.ords
SET s1 = :devolvidoNo
WHERE storeno = :storeno
  AND no = :ordno;

UPDATE sqldados.ordsRessu
SET s1 = :devolvidoNo
WHERE storeno = :storeno
  AND no = :ordno

