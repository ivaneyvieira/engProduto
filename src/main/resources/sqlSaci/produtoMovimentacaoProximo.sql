USE sqldados;

SELECT MAX(numero + 1) AS quant
FROM
  produtoMovimentacao
WHERE numloja = :numLoja