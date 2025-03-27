SELECT count(*) as quant
FROM
  sqldados.produtoEstoqueAcerto
WHERE numloja = :numLoja
  AND prdno = :prdno
  AND grade = :grade
  AND data = :data