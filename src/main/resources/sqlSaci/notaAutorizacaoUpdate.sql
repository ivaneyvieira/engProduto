USE sqldados;

UPDATE sqldados.nfAutorizacao
SET usernoSing = :usernoSing,
    tipoDev    = :tipoDev,
    observacao = :observacao
WHERE storeno = :loja
  AND pdvno = :pdv
  AND xano = :transacao
