UPDATE sqldados.produtoMovimentacao
SET impresso = 1
WHERE numero = :numero
  AND numloja = :numloja