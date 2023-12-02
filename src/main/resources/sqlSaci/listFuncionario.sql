SELECT E.no   AS codigo,
       E.name AS nome,
       F.name AS funcao
FROM sqldados.emp AS E
       INNER JOIN sqldados.empfnc AS F
                  ON E.funcao = F.no
WHERE E.no = :codigo