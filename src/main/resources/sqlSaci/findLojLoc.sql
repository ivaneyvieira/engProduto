SELECT DISTINCT storeno, MID(localizacao, 1, 4) AS loc
FROM
  sqldados.prdloc
