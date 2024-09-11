USE sqldados;

REPLACE sqldados.eoprdAdicional(storeno, ordno, prdno, grade, marca, qtRecebido, selecionado, posicao, empRecebido,
                                empEntregue)
VALUES (:loja, :numero, :prdno, :grade, :marca, :qtRecebido, :selecionado, :posicao, :recebidoNo, :entregueNo);

UPDATE
  sqldados.eoprdAdicional
SET marca = 1
WHERE empEntregue > 0
  AND empRecebido > 0
  AND selecionado > 0
  AND marca = 0
  AND storeno = :loja
  AND ordno = :numero
  AND prdno = :prdno
  AND grade = :grade