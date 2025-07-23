UPDATE sqldados.nf
SET l16 = :entrega,
    s16 = :empnoM,
    s14 = :separado
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano