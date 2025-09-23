DELETE
FROM
  sqldados.produtoKardec
WHERE (loja = :loja OR :loja = 0)
  AND loja IN (2, 3, 4, 5, 8)
  AND prdno = :prdno
  AND grade = :grade