USE sqldados;

DELETE
FROM
  sqldados.nfAutorizacao
WHERE storeno = :loja
  AND pdvno = :pdv
  AND xano = :transacao
