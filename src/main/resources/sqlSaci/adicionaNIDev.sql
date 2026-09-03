DO @NIDEV := CONCAT('PED ', :niDev);

UPDATE sqldados.nf
SET remarks = TRIM(MID(CONCAT('PED ', :niDev, ' ', remarks), 1, 40))
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND tipo IN (2)
  AND status != 1