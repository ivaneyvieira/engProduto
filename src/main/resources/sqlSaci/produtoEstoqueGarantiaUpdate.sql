USE sqldados;

REPLACE INTO produtoEstoqueGarantia (numero, numloja, data, hora, usuario, prdno, grade, estoqueSis)
  VALUE (:numero, :numloja, :data, :hora, :usuario, :prdno, :grade, :estoqueSis)
