SELECT TRIM(prdno)                AS codigo,
       TRIM(MID(prd.name, 1, 37)) AS descricao,
       grade                      AS grade,
       (qtty_varejo / 1000)       AS saldo
FROM sqldados.stk
       INNER JOIN sqldados.prd
                  ON prd.no = stk.prdno
WHERE storeno = 4
  AND grade != ''
  AND qtty_varejo != 0
  AND prdno = LPAD(:codigo, 16, ' ')
