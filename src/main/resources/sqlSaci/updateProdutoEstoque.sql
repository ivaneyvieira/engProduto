REPLACE INTO sqldados.prdAdicional(storeno, prdno, grade, estoque, localizacao, dataInicial, dataUpdate, kardec,
                                   dataObservacao, observacao)
VALUE (:loja, :prdno, :grade, :estoque, :locApp, :dataInicial, :dataUpdate, :kardec, :dataObservacao, :observacao)