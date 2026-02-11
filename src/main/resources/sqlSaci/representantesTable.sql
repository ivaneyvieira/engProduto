USE sqldados;

DROP TABLE IF EXISTS sqldados.repAdicional;
CREATE TABLE sqldados.repAdicional
(
  repno     int NOT NULL PRIMARY KEY,
  emailList varchar(1000) DEFAULT ''
);

