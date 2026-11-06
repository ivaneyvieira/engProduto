SELECT DISTINCT UPPER(TRIM(MID(localizacao, 1, 4))) AS locAbrev
FROM sqldados.prdAdicional
WHERE LENGTH(TRIM(MID(localizacao, 1, 4))) = 4
  AND localizacao LIKE 'CD%'
  AND localizacao LIKE CONCAT(MID(:local, 1, 4), '%')
GROUP BY localizacao
ORDER BY locAbrev

