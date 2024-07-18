UPDATE sqldados.nf
SET  s15 = :userPrint
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano