SELECT TRIM(prdno)          AS codigo,
       grade                AS grade,
       (qtty_varejo / 1000) AS saldo
FROM sqldados.stk
WHERE storeno = 4
  AND grade != ''
  AND qtty_varejo > 0
  AND prdno = LPAD(:codigo, 16, ' ')
