SELECT DISTINCT UPPER(TRIM(MID(localizacao, 1, 4))) AS locAbrev
FROM sqldados.prdAdicional
WHERE LENGTH(TRIM(MID(localizacao, 1, 4))) = 4
  AND localizacao LIKE 'CD%'
  AND localizacao LIKE CONCAT(:local, '%')
GROUP BY localizacao
ORDER BY locAbrev

