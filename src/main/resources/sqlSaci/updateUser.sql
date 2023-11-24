DO @NO := (SELECT MAX(no)
           FROM sqldados.users
           WHERE login = :login
             AND (name NOT LIKE '%INATIVO%'
             OR (bits1 & 1) != 0));

UPDATE sqldados.users
SET auxLong1 = :loja
WHERE no = @NO;

INSERT INTO sqldados.userApp(userno, appName, bitAcesso, locais)
VALUES (@NO, :appName, :bitAcesso, :locais)
ON DUPLICATE KEY UPDATE bitAcesso   = :bitAcesso,
                        locais      = :locais,
                        impressoras = :listaImpressora,
                        lojas       = :listaLoja