SELECT MID(localizacao, 1, 4) AS abreviacao
FROM sqldados.prdloc
WHERE storeno IN (2, 3, 4, 5)
GROUP BY abreviacao