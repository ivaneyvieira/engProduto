SELECT MID(localizacao, 1, 4) AS abreviacao
FROM sqldados.prdloc
WHERE storeno = 4
GROUP BY abreviacao