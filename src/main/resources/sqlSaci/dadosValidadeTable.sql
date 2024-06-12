
CREATE TABLE sqldados.dadosValidade
(
  seq         INT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  storeno     INT      NOT NULL,
  prdno       CHAR(16) NOT NULL,
  grade       CHAR(8)  NOT NULL,
  vencimento  INT      NULL,
  inventario  INT      NULL,
  dataEntrada INT      NULL
);

