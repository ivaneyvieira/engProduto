SELECT storeno, prdno, grade, num, quantidade, vencimento
FROM sqldados.qtd_vencimento
WHERE storeno IN (2, 3, 4, 5, 8)
  AND (quantidade IS NOT NULL
  OR vencimento IS NOT NULL)