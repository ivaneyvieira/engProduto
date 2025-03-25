USE sqldados;

REPLACE INTO produtoEstoqueAcertoMobile (numero, numloja, lojaSigla, data, hora, login, usuario, prdno, descricao, grade,
                                   estoqueSis, estoqueCd, estoqueLoja, diferenca, gravadoLogin, gravado)
  VALUE (:numero, :numloja, :lojaSigla, :data, :hora, :login, :usuario, :prdno, :descricao, :grade, :estoqueSis,
         :estoqueCD, :estoqueLoja, :diferenca, :gravadoLogin, :gravado)
