ALTER TABLE sqldados.userApp
  ADD senhaApp VARCHAR(40) NULL;

SELECT *
FROM
  sqldados.userApp
WHERE appName = 'engProduto';

DROP TABLE sqldados.userSaciApp;
CREATE TABLE sqldados.`userSaciApp`
(
  `no`              smallint                          NOT NULL DEFAULT '0',
  `appName`         varchar(30) CHARACTER SET latin1  NOT NULL,
  `name`            varchar(100) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `login`           varchar(40) CHARACTER SET latin1  NOT NULL DEFAULT '',
  `storeno`         int                               NOT NULL DEFAULT '0',
  `senha`           varchar(40)                                DEFAULT NULL,
  `bitAcesso`       bigint                            NOT NULL DEFAULT '0',
  `bitAcesso2`      bigint                            NOT NULL DEFAULT '0',
  `bitAcesso3`      bigint                            NOT NULL DEFAULT '0',
  `locais`          text CHARACTER SET latin1         NOT NULL,
  `impressora`      char(16) CHARACTER SET latin1              DEFAULT '',
  `listaImpressora` text CHARACTER SET latin1         NOT NULL,
  `ativoSaci`       varchar(3)                        NOT NULL DEFAULT '',
  `listaLoja`       text CHARACTER SET latin1         NOT NULL,
  PRIMARY KEY (`no`, `appName`),
  INDEX (appName, `login`)
) ENGINE = InnoDB;

