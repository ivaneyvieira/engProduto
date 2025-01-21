/*
CREATE TABLE sqldados.nfUserPrint
(
  storeno SMALLINT DEFAULT 0 NOT NULL,
  pdvno   SMALLINT DEFAULT 0 NOT NULL,
  xano    INT      DEFAULT 0 NOT NULL,
  userno  INT      DEFAULT 0 NOT NULL,
  PRIMARY KEY (storeno, pdvno, xano)
)

alter table sqldados.nfUserPrint
add COLUMN usernoSing INT DEFAULT 0 NOT NULL

alter table sqldados.nfUserPrint
add COLUMN usernoSingExt INT DEFAULT 0 NOT NULL

 */

REPLACE sqldados.nfUserPrint(storeno, pdvno, xano, userno, usernoSing, usernoSingExt)
VALUES (:storeno, :pdvno, :xano, :userPrint, :usernoSing, :usernoSingExp)

