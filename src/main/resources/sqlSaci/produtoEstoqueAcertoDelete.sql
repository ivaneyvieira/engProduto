USE sqldados;

DELETE
FROM
  produtoEstoqueAcerto
WHERE numero = :numero
  AND numloja = :numLoja