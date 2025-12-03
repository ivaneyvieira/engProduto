USE sqldados;

UPDATE sqldados.nfAutorizacao
SET nfEntRet = :nfEntRet
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano


