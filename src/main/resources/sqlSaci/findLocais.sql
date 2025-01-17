SELECT DISTINCT MID(localizacao, 1, 4) AS abreviacao
FROM
  sqldados.prdloc
WHERE
    storeno IN (4)
AND localizacao != ''
UNION
DISTINCT
SELECT DISTINCT MID(localizacao, 1, 4) AS abreviacao
FROM
  sqldados.prdAdicional
WHERE
    storeno IN (4)
AND localizacao != ''
