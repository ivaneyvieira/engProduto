UPDATE sqldados.nf
SET l16 = :entrega,
    s16 = :empnoM,
    s15 = :userPrint
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano