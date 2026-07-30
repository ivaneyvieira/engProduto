DELETE
FROM sqldados.nfAutorizacao
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND (invno = :invno OR invno = 0)