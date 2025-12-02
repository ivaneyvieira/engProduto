INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, dataInicial, qtConferencia, dataUpdate, qtConfEditLoja,
                                    dataObservacao)
  VALUE (:loja, :prdno, :grade, :dataInicial, :qtConferencia, :dataUpdate, :qtConfEditLoja,
         IF(:dataConferencia = 0, NULL, :dataConferencia));

UPDATE sqldados.prdAdicional
SET dataInicial    = :dataInicial,
    qtConferencia  = :qtConferencia,
    qtConfEditLoja = :qtConfEditLoja,
    qtConfEdit     = :qtConfEdit,
    dataUpdate     = :dataUpdate,
    dataObservacao = IF(:dataConferencia = 0, NULL, :dataConferencia)
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
