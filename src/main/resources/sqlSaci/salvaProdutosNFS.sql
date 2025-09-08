UPDATE sqldados.xaprd2 AS X
SET X.c6  = :gradeAlternativa,
    X.c3  = :usuarioSep,
    X.s4  = :usuarioCD,
    X.s5  = :usuarioExp,
    X.c5  = IF(X.c5 = '', CONCAT('-', DATE_FORMAT(NOW(), '%d/%m/%Y-%H:%i')), X.c5),
    X.c4  = IF(X.c4 = '', CONCAT('-', DATE_FORMAT(NOW(), '%d/%m/%Y-%H:%i')), X.c4)
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade;

REPLACE sqldados.xaprd2Marca(storeno, pdvno, xano, prdno, grade, marca, impresso)
SELECT :storeno, :pdvno, :xano, LPAD(:codigo, 16, ' ') AS prdno, :grade, :marca, :marcaImpressao
