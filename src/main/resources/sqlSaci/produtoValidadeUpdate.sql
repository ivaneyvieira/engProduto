UPDATE sqldados.produtoValidade
SET estoque = :estoque
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND vencimento = :vencimento