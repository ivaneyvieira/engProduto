SELECT storeno,
       CONCAT(nfno, '/', nfse) AS nota,
       CAST(issuedate AS date) AS emissao,
       grossamt / 100          AS valor,
       print_remarks           AS obs
FROM
  sqldados.nf
WHERE storeno IN (1, 2, 3, 4, 5, 8)
  AND issuedate >= 20241001
  AND tipo = 2
  AND print_remarks REGEXP '[^0-9][0-9]+/[0-9]+[^0-9]'