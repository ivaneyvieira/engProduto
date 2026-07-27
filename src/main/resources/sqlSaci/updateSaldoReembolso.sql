USE sqldados;

UPDATE sqldados.nf
SET remarks = TRIM(CONCAT(remarks, ' NI ', :invno, '-', :nfdev, ' ', :tipo))
WHERE storeno = :loja
  AND nfno = :nfno
  AND nfse = :nfse
  AND remarks NOT LIKE CONCAT('%', :tipo, '%')
