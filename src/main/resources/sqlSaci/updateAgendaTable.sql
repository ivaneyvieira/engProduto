DROP TABLE IF EXISTS sqldados.invAgenda;
CREATE TABLE sqldados.invAgenda
(
  invno               int PRIMARY KEY,
  data                int, /*c1*/
  hora                int, /*c2*/
  recebedor           int, /*auxStr6*/
  dataRecbedor        int, /*c3*/
  horaRecebedor       int,/*c4*/
  conhecimento        varchar(40),/*c5*/
  emissaoConhecimento int,
  coleta              int/*c6*/
);
