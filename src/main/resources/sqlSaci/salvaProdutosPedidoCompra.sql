UPDATE sqldados.eoprd AS X
SET X.c2  = :gradeAlternativa,
    X.s12 = :marca,
    X.c4  = :usuarioCD,
    X.c3  = :usuarioExp
WHERE storeno = :storeno
  AND ordno = :ordno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade