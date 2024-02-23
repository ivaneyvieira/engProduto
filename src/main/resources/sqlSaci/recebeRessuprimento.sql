UPDATE sqldados.ords
SET s2 = :recebidoNo
WHERE storeno = :storeno
  AND no = :ordno;

UPDATE sqldados.ordsRessu
SET s2 = :recebidoNo
WHERE storeno = :storeno
  AND no = :ordno

