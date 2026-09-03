SELECT storeno, pdvno, xano
FROM sqldados.nf
WHERE storeno = :loja
  AND nfno = :numero
  AND nfse = :serie
  AND tipo IN (2)
  AND status != 1
ORDER BY issuedate DESC