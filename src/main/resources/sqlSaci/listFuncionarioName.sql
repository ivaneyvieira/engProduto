SELECT E.no                                                                                                 AS codigo,
       E.name                                                                                               AS nome,
       E.sname                                                                                              AS login,
       F.name                                                                                               AS funcao,
       TRIM(IFNULL(CAST(CONCAT(CHAR(ASCII(SUBSTRING(pswd, 1, 1)) + ASCII('e') - ASCII('j')),
                               CHAR(ASCII(SUBSTRING(pswd, 2, 1)) + ASCII('a') - ASCII('h')),
                               CHAR(ASCII(SUBSTRING(pswd, 3, 1)) + ASCII('c') - ASCII('k')),
                               CHAR(ASCII(SUBSTRING(pswd, 4, 1)) + ASCII(' ') - ASCII(' ')),
                               CHAR(ASCII(SUBSTRING(pswd, 5, 1)) + ASCII(' ') - ASCII('B')),
                               CHAR(ASCII(SUBSTRING(pswd, 6, 1)) + ASCII(' ') - ASCII(')')),
                               CHAR(ASCII(SUBSTRING(pswd, 7, 1)) + ASCII(' ') - ASCII(')')),
                               CHAR(ASCII(SUBSTRING(pswd, 8, 1)) + ASCII(' ') - ASCII('-'))) AS CHAR), '')) AS senha
FROM
  sqldados.emp                 AS E
    INNER JOIN sqldados.empfnc AS F
               ON E.funcao = F.no
WHERE E.sname = :nome
  AND E.sname != ''