DELETE
FROM
  sqldados.produtoEstoqueAcertoMobile
WHERE numloja = :numLoja
  AND numero = :numero
  AND prdno = :prdno
  AND grade = :grade