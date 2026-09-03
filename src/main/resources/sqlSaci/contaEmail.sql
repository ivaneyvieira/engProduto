SELECT chave, COUNT(*) AS quant
FROM sqldados.emailDevolucao
where chave = :chave
GROUP BY chave