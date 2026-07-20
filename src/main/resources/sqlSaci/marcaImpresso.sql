UPDATE sqldados.inv
SET c9 = CONCAT(:marca, '/', TIME_FORMAT(CURRENT_TIME, '%H:%i'))
WHERE invno = :invno;

UPDATE sqldados.nf
SET remarks = MID(CONCAT('NI ', :invno, ' ', remarks), 1, 40)
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND remarks NOT LIKE CONCAT('NI ', :invno, ' %')