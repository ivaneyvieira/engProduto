UPDATE sqldados.xaprd2 AS X
SET X.c6  = :gradeAlternativa,
    X.s11 = :marca,
    X.s10 = :marcaImpressao,
    X.c3 = :usuarioSep,
    X.c4  = :usuarioCD,
    X.c5  = :usuarioExp
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade