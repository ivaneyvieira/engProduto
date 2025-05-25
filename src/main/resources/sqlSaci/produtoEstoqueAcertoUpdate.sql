USE sqldados;

REPLACE INTO produtoEstoqueAcerto (numero, numloja, data, hora, login, acertoSimples, usuario, prdno, grade,
                                   estoqueSis, estoqueCd, estoqueLoja, gravadoLogin, gravado)
  VALUE (:numero, :numloja, :data, :hora, :login, :acertoSimples, :usuario, :prdno, :grade,
         :estoqueSis, :estoqueCD, :estoqueLoja, :gravadoLogin, :gravado)
