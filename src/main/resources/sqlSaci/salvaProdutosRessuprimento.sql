UPDATE sqldados.oprd AS X
SET X.auxShort4 = :marca,
    X.obs       = :usuarioCD,
    X.remarks   = :usuarioExp
WHERE storeno = :storeno
  AND ordno = :ordno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade