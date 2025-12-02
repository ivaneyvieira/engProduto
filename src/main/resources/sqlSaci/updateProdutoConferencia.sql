INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, dataInicial, qtConferencia, dataUpdate, qtConfEditLoja,
                                    dataObservacao, estoqueConfCD, estoqueConfLoja)
  VALUE (:loja, :prdno, :grade, :dataInicial, :qtConferencia, :dataUpdate, :qtConfEditLoja,
         IF(:dataConferencia = 0, NULL, :dataConferencia), :estoqueConfCD, :estoqueConfLoja);

UPDATE sqldados.prdAdicional
SET dataInicial     = :dataInicial,
    qtConferencia   = :qtConferencia,
    qtConfEditLoja  = :qtConfEditLoja,
    dataUpdate      = :dataUpdate,
    estoqueConfCD   = :estoqueConfCD,
    estoqueConfLoja = :estoqueConfLoja,
    dataObservacao  = IF(:dataConferencia = 0, NULL, :dataConferencia)
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
