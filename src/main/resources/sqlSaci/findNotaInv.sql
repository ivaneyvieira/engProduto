SELECT invno AS ni, storeno AS loja, nfname AS nfno, invse AS nfse
FROM sqldados.inv
WHERE invno = :ni