SELECT *
FROM sqldados.stkmov AS M
       INNER JOIN sqldados.prd AS P
                  ON P.no = M.prdno
WHERE