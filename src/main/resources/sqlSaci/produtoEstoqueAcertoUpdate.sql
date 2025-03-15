USE sqldados;


REPLACE INTO produtoEstoqueAcerto (numero, numloja, lojaSigla, data, hora, login, usuario, prdno, descricao, grade,
                                   diferenca) VALUE (:numero, :numloja, :lojaSigla, :data, :hora, :login, :usuario,
                                                     :prdno, :descricao, :grade, :diferenca)
