USE sqldados;

REPLACE INTO sqldados.eordAdicional (storeno, ordno, localizacao, empEntregue, empRecebido, observacao)
VALUES (:loja, :numero, :localizacao, :entregueNo, :recebidoNo, :observacao)
