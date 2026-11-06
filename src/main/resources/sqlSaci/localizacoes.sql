SELECT DISTINCT TRIM(MID(localizacao, 1, 4)) AS locAbrev
FROM sqldados.prdAdicional
WHERE LENGTH(TRIM(MID(localizacao, 1, 4))) = 4
  AND localizacao LIKE 'CD%'
ORDER BY locAbrev