UPDATE sqldados.iprd
SET c10 = IF(:temProduto, 'TROCA P', 'TROCA')
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
