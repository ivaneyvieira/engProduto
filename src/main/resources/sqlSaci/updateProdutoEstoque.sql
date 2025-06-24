REPLACE INTO sqldados.prdAdicional(storeno, prdno, grade, estoque, localizacao, dataInicial, dataUpdate, kardec,
                                   dataObservacao, observacao, estoqueUser, estoqueData, estoqueCD, estoqueLoja)
  VALUE (:loja, :prdno, :grade, :estoque, IFNULL(:locApp, ''), :dataInicial, :dataUpdate, :kardec, :dataConferencia,
         :qtConferencia, :estoqueUser, :estoqueData, :estoqueCD, :estoqueLoja)