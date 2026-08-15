USE sqldados;

SET sql_mode = '';

DELETE
FROM sqldados.dadosDevProduto
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
  AND produtoTroca = :produtoTroca