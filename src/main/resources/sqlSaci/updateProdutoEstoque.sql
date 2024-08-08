REPLACE INTO sqldados.prdAdicional(storeno, prdno, grade, estoque, localizacao, dataInicial)
SELECT :loja, :prdno, :grade, :estoque, :locApp, :dataInicial
