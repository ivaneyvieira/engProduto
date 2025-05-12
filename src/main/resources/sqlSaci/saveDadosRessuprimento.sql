UPDATE
  sqldados.oprd
SET qtty = :qttyPedida
WHERE storeno = :loja
  AND ordno = :pedido
  AND prdno = :prdno
  AND grade = :grade
  AND seqno = :seqno