USE
sqldados;

INSERT
IGNORE sqldados.nfAutorizacao(storeno, pdvno, xano, usernoSing, tipoDev, dataInsert)
SELECT storeno, pdvno, xano, 0 AS usernoSing, '' AS tipoDev, CURRENT_DATE * 1 AS dataInsert
FROM sqldados.nf
WHERE storeno = :loja
  AND nfno = :nfno
  AND nfse = :nfse
