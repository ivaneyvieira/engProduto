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
       estoqueSis,
       estoqueCD,
       estoqueLoja,
       diferenca,
       IF(processado, 'Sim', 'Não') AS processado,
       transacao
FROM
  sqldados.produtoEstoqueAcerto
WHERE numloja = :numLoja
  AND prdno = :prdno
  AND grade = :grade
  AND data = :data