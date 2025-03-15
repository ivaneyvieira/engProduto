SELECT numero,
       numloja,
       lojaSigla,
       data,
       hora,
       usuario,
       prdno,
       descricao,
       grade,
       diferenca,
       IF(processado, 'Sim', 'Não') AS processado,
       transacao
FROM
  sqldados.produtoEstoqueAcerto
WHERE numloja = :numLoja
  AND prdno = :prdno
  AND grade = :grade
  AND data = :data
  AND diferenca = :diferenca