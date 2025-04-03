SELECT CONCAT(nfno, '/', nfse) AS nota, CAST(issuedate AS date) AS emissao, grossamt / 100 AS valor,
       print_remarks as obs
FROM
  sqldados.nf
WHERE storeno in (1, 2, 3, 4, 5, 8)
  AND issuedate >= 20250101
  AND tipo = 2
