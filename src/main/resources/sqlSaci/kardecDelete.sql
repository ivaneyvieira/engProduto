DELETE
FROM
  sqldados.produtoKardec
WHERE loja = :loja
  AND prdno = :prdno
  AND grade = :grade