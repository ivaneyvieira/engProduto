SELECT invno AS ni, storeno as loja, nfname AS nfno, invse AS nfse
FROM sqldados.inv
WHERE storeno = :loja
  AND nfname = :nfno
  AND invse = :nfse
ORDER BY date desc