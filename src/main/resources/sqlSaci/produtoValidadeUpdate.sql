REPLACE INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque, compras)
SELECT :storeno,
       :prdno,
       :grade,
       :dataEntrada,
       :vencimento,
       :estoque,
       :compras
FROM dual