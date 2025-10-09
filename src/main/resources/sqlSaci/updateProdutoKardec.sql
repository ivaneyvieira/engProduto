INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, kardec, dataUpdate)
  VALUE (:loja, :prdno, :grade, :kardec, :dataUpdate);

UPDATE sqldados.prdAdicional
SET kardec     = :kardec,
    dataUpdate = :dataUpdate
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
