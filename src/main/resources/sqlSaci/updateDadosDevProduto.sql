USE sqldados;

SET sql_mode = '';

REPLACE sqldados.dadosDevProduto(invno, prdno, grade, produtoTroca, quantidade) VALUE (:invno, :prdno, :grade, :produtoTroca, :quantidade)
