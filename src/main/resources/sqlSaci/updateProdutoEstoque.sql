REPLACE INTO sqldados.prdAdicional(storeno, prdno, grade, estoque, localizacao, dataInicial, dataUpdate, kardec,
                                   dataObservacao, observacao, estoqueData, estoqueCD, estoqueLoja)
VALUE (:loja, :prdno, :grade, :estoque, IFNULL(:locApp, ''), :dataInicial, :dataUpdate, :kardec,
       :dataObservacao, :observacao, :estoqueData, :estoqueCD, :estoqueLoja)