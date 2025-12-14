use sqldados;

CREATE TABLE prdControle
(
  storeno     int           NOT NULL,
  prdno       varchar(16)   NOT NULL,
  grade       varchar(10)   NOT NULL,
  dataInicial int DEFAULT 0 NULL,
  estoqueLoja int           NULL,
  PRIMARY KEY (storeno, prdno, grade)
)
  CHARSET = latin1;

