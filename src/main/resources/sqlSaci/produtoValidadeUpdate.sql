REPLACE INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque)
SELECT :storeno,
       :prdno,
       :grade,
       :dataEntrada,
       :vencimento,
       :estoque
FROM dual