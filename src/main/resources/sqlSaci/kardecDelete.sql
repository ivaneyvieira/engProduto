DELETE
FROM
  sqldados.produtoKardec
WHERE (loja = :loja OR :loja = 0)
  AND prdno = :prdno
  AND grade = :grade