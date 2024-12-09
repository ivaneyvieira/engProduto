USE sqldados;

SELECT prdno, MID(localizacao, 1, 4) AS loc, P.clno, P.mfno AS vendno, COUNT(*) AS qtde
FROM
  sqldados.prdloc           AS L
    INNER JOIN sqldados.prd AS P
               ON L.prdno = P.no
WHERE localizacao REGEXP 'CD[0-9][A-Z]'
GROUP BY prdno, loc
