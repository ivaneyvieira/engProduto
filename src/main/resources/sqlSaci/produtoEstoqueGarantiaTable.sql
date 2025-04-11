USE sqldados;

DROP TABLE IF EXISTS produtoEstoqueGarantia;
CREATE TABLE produtoEstoqueGarantia
(
  numero      int         NOT NULL,
  numloja     int         NOT NULL,
  data        date        NOT NULL,
  hora        time        NOT NULL,
  usuario     varchar(50) NOT NULL,
  prdno       varchar(16) NOT NULL,
  grade       varchar(20) NOT NULL,
  estoqueSis  int         NULL,
  estoqueReal int         NULL,
  PRIMARY KEY (numloja, numero, prdno, grade)
);
