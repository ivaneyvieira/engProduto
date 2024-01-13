USE sqldados;

INSERT IGNORE sqldados.nfAutorizacao(storeno, pdvno, xano)
SELECT storeno, pdvno, xano
FROM sqldados.nf
WHERE storeno = :loja
  AND pdvno = :pdv
  AND xano = :transacao
