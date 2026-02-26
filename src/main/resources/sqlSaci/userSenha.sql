SELECT U.no,
       U.name,
       login,
       U.auxLong1                                AS storeno,
       IF(A.senhaApp IS NULL,
          TRIM(IFNULL(CAST(CONCAT(CHAR(ASCII(SUBSTRING(pswd, 1, 1)) + ASCII('e') - ASCII('j')),
                                  CHAR(ASCII(SUBSTRING(pswd, 2, 1)) + ASCII('a') - ASCII('h')),
                                  CHAR(ASCII(SUBSTRING(pswd, 3, 1)) + ASCII('c') - ASCII('k')),
                                  CHAR(ASCII(SUBSTRING(pswd, 4, 1)) + ASCII(' ') - ASCII(' ')),
                                  CHAR(ASCII(SUBSTRING(pswd, 5, 1)) + ASCII(' ') - ASCII('B')),
                                  CHAR(ASCII(SUBSTRING(pswd, 6, 1)) + ASCII(' ') - ASCII(')')),
                                  CHAR(ASCII(SUBSTRING(pswd, 7, 1)) + ASCII(' ') - ASCII(')')),
                                  CHAR(ASCII(SUBSTRING(pswd, 8, 1)) + ASCII(' ') - ASCII('-'))) AS CHAR), '')),
          A.senhaApp)                            AS senha,
       IFNULL(A.bitAcesso, 0)                    AS bitAcesso,
       IFNULL(A.bitAcesso2, 0)                   AS bitAcesso2,
       IFNULL(A.bitAcesso3, 0)                   AS bitAcesso3,
       IFNULL(A.locais, '')                      AS locais,
       P.name                                    AS impressora,
       IFNULL(impressoras, '')                   AS listaImpressora,
       IF(U.bits1 & POW(0, 2) = 0, 'Sim', 'Não') AS ativoSaci,
       IFNULL(lojas, '')                         AS listaLoja
FROM
  sqldados.users               AS U
    LEFT JOIN sqldados.prntr   AS P
              ON P.no = U.prntno
    LEFT JOIN sqldados.userApp AS A
              ON A.userno = U.no AND A.appName = :appName
WHERE (login = :login OR :login = 'TODOS')
  AND U.bits1 & POW(2, 0) = 0