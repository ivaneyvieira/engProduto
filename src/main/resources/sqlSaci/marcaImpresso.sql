UPDATE sqldados.inv
SET c9 = CONCAT(:marca, '/', TIME_FORMAT(CURRENT_TIME, '%H:%i'))
WHERE invno = :invno