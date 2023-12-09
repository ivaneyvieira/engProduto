select *
from sqldados.stkmov AS M
inner join sqldados.prd AS P
ON P.no = M.prdno
where