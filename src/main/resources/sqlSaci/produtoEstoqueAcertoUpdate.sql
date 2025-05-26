USE sqldados;

REPLACE INTO produtoEstoqueAcerto (numero, numloja, data, hora, login, acertoSimples, usuario, prdno, grade,
                                   estoqueSis, estoqueCD, diferenca, estoqueLoja, gravadoLogin, gravado)
  VALUE (:numero, :numloja, :data, :hora, :login, :acertoSimples, :usuario, :prdno, :grade,
         :estoqueSis, :estoqueCD, :diferenca, :estoqueLoja, :gravadoLogin, :gravado)
