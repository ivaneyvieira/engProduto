UPDATE sqldados.xaprd2 AS X
SET X.c6  = :gradeAlternativa,
    X.s11 = :marca,
    X.c4  = :usuarioCD,
    X.c5  = :usuarioExp,
    X.l12 = :quantidadeEdt * 1000
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND prdno = :prdno
  AND grade = :grade