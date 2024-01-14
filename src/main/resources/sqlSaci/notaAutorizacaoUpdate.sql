USE sqldados;

UPDATE sqldados.nfAutorizacao
SET usernoSing = :usernoSing,
    tipoDev    = :tipoDev
WHERE storeno = :loja
  AND pdvno = :pdv
  AND xano = :transacao
