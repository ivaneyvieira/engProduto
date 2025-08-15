UPDATE sqldados.iprd
SET c10 = CONCAT(:tipo, IF(:temProduto, ' P', ''))
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
