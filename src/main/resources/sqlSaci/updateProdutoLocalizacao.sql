INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, localizacao)
  VALUE (:loja, :prdno, :grade, :localizacao);

UPDATE sqldados.prdAdicional
SET localizacao   = :localizacao
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
