SELECT count(*) as quant
FROM
  sqldados.produtoEstoqueGarantia
WHERE numloja = :numLoja
  AND prdno = :prdno
  AND grade = :grade
  AND data = :data