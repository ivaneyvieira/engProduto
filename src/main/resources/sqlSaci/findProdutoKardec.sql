DO @DATA_FINAL := ROUND(CURDATE() * 1);


SELECT storeno            AS loja,
       prdno              AS prdno,
       grade              AS grade,
       CAST(date AS date) AS data,
       doc                AS doc,
       'VENDA'            AS tipo,
       ROUND(qtty / 1000) AS qtde,
       0                  AS saldo
FROM
  sqldados.xalog2 AS X
WHERE prdno = :prdno
  AND grade = :grade
  AND storeno = :loja
  AND date BETWEEN :dataInicial AND @DATA_FINAL
  AND qtty > 0
ORDER BY date, time, xano