DO @NO := (SELECT MAX(no)
           FROM sqldados.users
           WHERE login = :login
             AND (bits1 & POW(2, 0)) = 0);

UPDATE sqldados.users
SET auxLong1 = :loja
WHERE no = @NO;

INSERT INTO sqldados.userApp(userno, appName, bitAcesso, locais)
VALUES (@NO, :appName, :bitAcesso, :locais)
ON DUPLICATE KEY UPDATE bitAcesso   = :bitAcesso,
                        locais      = :locais,
                        impressoras = :listaImpressora,
                        lojas       = :listaLoja