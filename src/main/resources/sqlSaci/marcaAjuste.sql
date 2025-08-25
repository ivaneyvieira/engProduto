UPDATE sqldados.iprd
SET c10 = CONCAT(CONCAT(:tipo, IF(:temProduto, ' P', '')), '|', :quant)
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
