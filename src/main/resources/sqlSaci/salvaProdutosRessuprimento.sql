UPDATE sqldados.oprd AS X
SET X.auxShort4 = :marca,
    X.obs       = :usuarioCD
WHERE storeno = 1
  AND ordno = :ordno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade