DELETE
FROM
  sqldados.produtoEstoqueGarantia
WHERE numloja = :numLoja
  AND numero = :numero
  AND prdno = :prdno
  AND grade = :grade