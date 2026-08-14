USE sqldados;

SET sql_mode = '';

DELETE
FROM sqldados.dadosDev
WHERE invno = :invno;

DELETE
FROM sqldados.dadosDevProduto
WHERE invno = :invno
