SELECT numero,
       numloja,
       lojaSigla,
       data,
       hora,
       login,
       usuario,
       prdno,
       descricao,
       grade,
       diferenca,
       IF(processado, 'Sim', 'Não') AS processado,
       transacao
FROM
  sqldados.produtoEstoqueAcerto
WHERE (numero = :numero OR :numero = 0)
  AND (numloja = :numLoja OR :numLoja = 0)
  AND (data = :data OR :data = 0)
  AND (descricao != '')