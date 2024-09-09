USE sqldados;

REPLACE sqldados.eoprdAdicional(storeno, ordno, prdno, grade, marca, qtRecebido, selecionado, posicao, empRecebido,
                                empEntregue)
VALUES (:loja, :numero, :prdno, :grade, :marca, :qtRecebido, :selecionado, :posicao, :recebidoNo, :entregueNo)