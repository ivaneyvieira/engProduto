SELECT MID(localizacao, 1, 4) AS abreviacao
FROM sqldados.prdloc
where storeno = 4
GROUP BY abreviacao