SELECT CONCAT(nfno, '/', nfse) AS nota, CAST(issuedate AS date) AS emissao, grossamt / 100 AS valor
FROM
  sqldados.nf
WHERE print_remarks LIKE CONCAT('%', :nota, '%')
  AND TRIM(:nota) != ''
  AND storeno = :loja
  AND issuedate >= :data
  AND tipo = 2