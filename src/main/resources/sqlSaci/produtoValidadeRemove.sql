DELETE
FROM sqldados.produtoValidade
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND IFNULL(vencimento, 0) = :vencimento
  AND tipo = :tipo
  AND dataEntrada = :dataEntrada
