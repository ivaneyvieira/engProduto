UPDATE sqldados.eoprd
SET status = :status
WHERE storeno = :loja
  AND ordno = :pedido
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade;

UPDATE sqldados.eoprd2
SET status = :status
WHERE storeno = :loja
  AND ordno = :pedido
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade;

UPDATE sqldados.eord
SET status = :status
WHERE storeno = :loja
  AND ordno = :pedido