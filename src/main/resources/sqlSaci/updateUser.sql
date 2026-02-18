UPDATE sqldados.users
SET auxLong1 = :storeno
WHERE no = :no;

INSERT INTO sqldados.userApp(userno, appName, bitAcesso, bitAcesso2, bitAcesso3, locais, senhaApp)
VALUES (:no, :appName, :bitAcesso, :bitAcesso2, :bitAcesso3, :locais, :senha)
ON DUPLICATE KEY UPDATE bitAcesso   = :bitAcesso,
                        bitAcesso2  = :bitAcesso2,
                        bitAcesso3  = :bitAcesso3,
                        locais      = :locais,
                        impressoras = :listaImpressora,
                        lojas       = :listaLoja;

REPLACE INTO sqldados.userSaciApp(no, appName, name, login, storeno, senha, bitAcesso, bitAcesso2, bitAcesso3, locais,
                                  impressora, listaImpressora, listaLoja)
VALUES (:no, :appName, :name, :login, :storeno, :senha, :bitAcesso, :bitAcesso2, :bitAcesso3, :locais, :impressora,
        :listaImpressora, :listaLoja)