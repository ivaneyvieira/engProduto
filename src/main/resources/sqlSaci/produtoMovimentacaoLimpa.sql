DELETE
FROM
  sqldados.produtoMovimentacao
WHERE numloja = :numLoja
  AND numero = :numero
  AND prdno = :prdno
  AND grade = :grade