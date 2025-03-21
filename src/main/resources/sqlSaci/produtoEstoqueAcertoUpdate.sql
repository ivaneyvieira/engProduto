USE sqldados;

REPLACE INTO produtoEstoqueAcerto (numero, numloja, lojaSigla, data, hora, login, usuario, prdno, descricao, grade,
                                   estoqueSis, estoqueCd, estoqueLoja, diferenca)
  VALUE (:numero, :numloja, :lojaSigla, :data, :hora, :login, :usuario, :prdno, :descricao, :grade, :estoqueSis,
         :estoqueCD, :estoqueLoja, :diferenca)
