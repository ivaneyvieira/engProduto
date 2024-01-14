USE sqldados;

INSERT IGNORE sqldados.nfAutorizacao(storeno, pdvno, xano, usernoSing, tipoDev)
SELECT storeno, pdvno, xano, 0 as usernoSing, '' as tipoDev
FROM sqldados.nf
WHERE storeno = :loja
  AND nfno = :nfno
  AND nfse = :nfse
