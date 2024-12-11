DELETE
FROM sqldados.oprd
WHERE storeno = 1
  AND ordno = :pedido
  AND prdno = :prdno
  AND grade = :grade