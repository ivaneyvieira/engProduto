USE sqldados;

REPLACE INTO produtoEstoqueGarantia (numero, numloja, data, hora, usuario, prdno, grade, estoqueSis, estoqueReal,
                                     loteDev)
  VALUE (:numero, :numloja, :data, :hora, :usuario, :prdno, :grade, :estoqueSis, :estoqueReal, :loteDev)
