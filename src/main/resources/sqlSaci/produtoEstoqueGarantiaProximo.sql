USE sqldados;

SELECT MAX(numero + 1) AS quant
FROM
  produtoEstoqueGarantia
WHERE numloja = :numLoja