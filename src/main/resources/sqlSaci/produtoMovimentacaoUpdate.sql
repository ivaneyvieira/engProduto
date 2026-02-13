USE sqldados;

REPLACE INTO produtoMovimentacao (numero, numloja, data, hora, login, usuario, prdno, grade, gravadoLogin, gravado)
  VALUE (:numero, :numloja, :data, :hora, :login, :usuario, :prdno, :grade, :gravadoLogin, :gravado)
