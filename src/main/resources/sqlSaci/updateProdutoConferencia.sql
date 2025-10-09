INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, dataInicial, qtConferencia, dataUpdate)
  VALUE (:loja, :prdno, :grade, :dataInicial, :qtConferencia, :dataUpdate);

UPDATE sqldados.prdAdicional
SET dataInicial   = :dataInicial,
    qtConferencia = :qtConferencia,
    dataUpdate    = :dataUpdate
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
