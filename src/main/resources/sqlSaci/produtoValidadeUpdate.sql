REPLACE INTO sqldados.produtoValidade(storeno, prdno, grade, vencimento, estoque)
VALUES (:storeno, :prdno, :grade, MID(:vencimento, 1, 6) * 1, :estoque)