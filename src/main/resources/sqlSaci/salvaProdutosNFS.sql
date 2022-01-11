UPDATE sqldados.xaprd2 AS X
SET X.c6  = :gradeAlternativa,
    X.s12 = :marca,
    X.c4  = :usuarioCD,
    X.c5  = :usuarioExp
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade