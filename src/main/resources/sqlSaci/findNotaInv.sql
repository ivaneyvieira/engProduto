SELECT invno AS ni, storeno as loja,  nfname AS nfno, invse AS nfse
FROM sqldados.inv
WHERE invno = :ni