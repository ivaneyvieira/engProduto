USE sqldados;

SELECT MAX(numero + 1) AS quant
FROM
  produtoEstoqueAcerto
WHERE numloja = :numLoja
  AND descricao != ''