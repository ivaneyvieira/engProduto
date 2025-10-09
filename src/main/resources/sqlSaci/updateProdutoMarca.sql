INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, estoqueUser, estoqueData)
  VALUE (:loja, :prdno, :grade, :estoqueUser, :estoqueData);

UPDATE sqldados.prdAdicional
SET estoqueUser   = :estoqueUser,
    estoqueData = :estoqueData
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
