DELETE
FROM
  sqldados.oprd
WHERE storeno = :loja
  AND ordno = :pedido
  AND prdno = :prdno
  AND grade = :grade
  AND seqno = :seqno