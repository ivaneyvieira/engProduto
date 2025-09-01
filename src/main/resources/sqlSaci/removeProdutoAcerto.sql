DELETE
FROM
  sqldados.oprd
WHERE storeno = :loja
  AND ordno = :pedido
  AND prdno = :prdno
  AND grade = :grade
  AND :loja != 1
  AND :pedido != 2;

UPDATE sqldados.oprd
SET qtty = IF(:lojaAcerto = 0, 0, REPLACE(qtty, :lojaAcerto, '') * 1)
WHERE storeno = :loja
  AND ordno = :pedido
  AND prdno = :prdno
  AND grade = :grade
  AND :loja = 1
  AND :pedido = 2