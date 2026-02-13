USE sqldados;

REPLACE INTO produtoMovimentacao (numero, numloja, data, hora, login, usuario, prdno, grade, gravadoLogin, gravado, movimentacao)
  VALUE (:numero, :numloja, :data, :hora, :login, :usuario, :prdno, :grade, :gravadoLogin, :gravado, :movimentacao)
