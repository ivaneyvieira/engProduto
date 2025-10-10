INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, dataInicial, qtConferencia, dataUpdate, qtConfEditLoja)
  VALUE (:loja, :prdno, :grade, :dataInicial, :qtConferencia, :dataUpdate, :qtConfEditLoja);

UPDATE sqldados.prdAdicional
SET dataInicial    = :dataInicial,
    qtConferencia  = :qtConferencia,
    qtConfEditLoja = :qtConfEditLoja,
    dataUpdate     = :dataUpdate
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
