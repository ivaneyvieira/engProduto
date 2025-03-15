USE sqldados;


REPLACE INTO produtoEstoqueAcerto (numero, numloja, lojaSigla, data, hora, usuario, prdno, descricao, grade,
                                   diferenca) VALUE (:numero, :numloja, :lojaSigla, :data, :hora, :usuario, :prdno,
                                                     :descricao, :grade, :diferenca)
