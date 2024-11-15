DROP TABLE IF EXISTS sqldados.pedidoNdd;
CREATE TABLE sqldados.pedidoNdd
(
  id     INT NOT NULL,
  loja   INT NULL,
  pedido INT NULL,
  PRIMARY KEY (id)
);


select * from sqldados.pedidoNdd;