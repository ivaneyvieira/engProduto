INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, estoqueCD, estoqueLoja)
  VALUE (:loja, :prdno, :grade, :estoqueCD, :estoqueLoja);

UPDATE sqldados.prdAdicional
SET estoqueCD   = :estoqueCD,
    estoqueLoja = :estoqueLoja
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
