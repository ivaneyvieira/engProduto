SELECT COUNT(*) AS quant
FROM
  sqldados.produtoMovimentacao
WHERE numloja = :numLoja
  AND prdno = :prdno
  AND grade = :grade
  AND data = :data