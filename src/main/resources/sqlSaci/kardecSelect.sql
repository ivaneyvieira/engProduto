SELECT loja,
       prdno,
       grade,
       data,
       doc,
       tipo,
       CAST(IF(vencimento * 1 = 0, NULL, vencimento * 1) AS DATE) AS vencimento,
       qtde,
       saldo,
       userLogin
FROM
  sqldados.produtoKardec
WHERE loja = :loja
  AND prdno = :prdno
  AND grade = :grade
  AND data < CURRENT_DATE * 1