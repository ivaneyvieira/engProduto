REPLACE INTO sqldados.prdAdicional(storeno, prdno, grade, estoque, localizacao, dataInicial, dataUpdate, kardec,
                                   dataObservacao, observacao, estoqueUser, estoqueData, estoqueCD,
                                   estoqueLoja)
  VALUE (:loja, :prdno, :grade, :estoque, IFNULL(:locApp, ''), :dataInicial, :dataUpdate, :kardec,
         NULL, CONCAT(:qtConferencia, ',', :qtConfEdit), :estoqueUser, :estoqueData,
         :estoqueCD, :estoqueLoja)