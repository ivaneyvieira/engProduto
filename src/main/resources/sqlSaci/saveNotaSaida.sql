USE sqldados;

UPDATE sqldados.nf
SET l16 = :entrada
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano