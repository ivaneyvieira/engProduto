SELECT numero, numloja, lojaSigla, data, hora, usuario, prdno, descricao, grade, diferenca
FROM
  sqldados.produtoEstoqueAcerto
WHERE (numero = :numero OR :numero = 0)
  AND (numloja = :numLoja OR :numLoja = 0)
  AND (data = :data OR :data = 0)
  AND (descricao != '')