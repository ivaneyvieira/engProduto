SELECT chave, COUNT(*) AS quant
FROM sqldados.emailDevolucao
WHERE chave = :chave
GROUP BY chave