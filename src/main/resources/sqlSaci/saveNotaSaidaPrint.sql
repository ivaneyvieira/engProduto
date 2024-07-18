/*
CREATE TABLE sqldados.nfUserPrint
(
  storeno SMALLINT DEFAULT 0 NOT NULL,
  pdvno   SMALLINT DEFAULT 0 NOT NULL,
  xano    INT      DEFAULT 0 NOT NULL,
  userno  INT      DEFAULT 0 NOT NULL,
  PRIMARY KEY (storeno, pdvno, xano)
)
 */

replace sqldados.nfUserPrint(storeno, pdvno, xano, userno)
values(:storeno, :pdvno, :xano, :userPrint)

