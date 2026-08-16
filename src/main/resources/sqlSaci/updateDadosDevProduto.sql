USE sqldados;

SET sql_mode = '';

REPLACE sqldados.dadosDevProduto(invno, prdno, grade, quantidadeCom, quantidadeSem) VALUE (:invno, :prdno, :grade, :quantidadeCom, :quantidadeSem)
