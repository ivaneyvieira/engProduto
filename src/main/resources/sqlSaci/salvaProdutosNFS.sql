UPDATE sqldados.nfSaidaAdiciona AS X
SET X.gradeAlternativa = :gradeAlternativa,
    X.marca            = :marca,
    X.usuarioCD        = :usuarioCD,
    X.usuarioExp       = :usuarioExp
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade