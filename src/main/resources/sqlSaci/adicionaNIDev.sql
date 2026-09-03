DO @NIDEV := CONCAT('NI DEV ', :niDev);

UPDATE sqldados.nf
SET remarks = TRIM(MID(CONCAT('NI DEV ', :niDev, ' ', remarks), 1, 40))
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND tipo IN (2)
  AND status != 1
  AND remarks NOT LIKE '%NI DEV %'