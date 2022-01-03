UPDATE sqldados.eoprdf
SET remarks = IF(:gradeAlternativa = :grade, '', :gradeAlternativa)
WHERE storeno = :storeno
  AND ordno = :ordno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade