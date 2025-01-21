/*
-- invno: Int, storeno: Int, pdvno: Int, xano: Int, user: UserSaci
create table sqldados.invAutorizacao(
  invno int not null,
  storeno int not null,
  pdvno int not null,
  xano int not null,
  userno int not null,
  PRIMARY KEY (invno, storeno, pdvno, xano)
)
*/

REPLACE INTO sqldados.invAutorizacao(invno, storeno, pdvno, xano, userno)
VALUES (:invno, :storeno, :pdvno, :xano, :userno)