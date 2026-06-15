UPDATE sqldados.inv
SET c9 = ''
WHERE invno = :invno;

UPDATE sqldados.nf
SET remarks = TRIM(REPLACE(remarks, CONCAT('NI ', :invno), ''))
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND remarks LIKE CONCAT('NI ', :invno, ' %')