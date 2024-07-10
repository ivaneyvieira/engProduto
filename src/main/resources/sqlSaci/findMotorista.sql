SELECT E.no AS empno, E.sname AS sname, E.name AS name, F.no AS fncno, F.name AS funcao
FROM sqldados.emp AS E
       INNER JOIN sqldados.empfnc AS F
                  ON E.funcao = F.no
WHERE F.name = 'MOTORISTA'