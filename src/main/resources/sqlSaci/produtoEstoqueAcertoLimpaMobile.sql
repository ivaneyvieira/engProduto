DELETE
FROM
  sqldados.produtoEstoqueAcerto
WHERE numloja = :numLoja
  AND numero = :numero
  AND prdno = :prdno
  AND grade = :grade