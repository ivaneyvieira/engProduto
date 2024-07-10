UPDATE sqldados.nf
SET l16 = :entrega,
    s16 = :empnoM
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano