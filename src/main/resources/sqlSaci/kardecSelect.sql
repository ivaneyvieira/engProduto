SELECT loja,
       prdno,
       grade,
       data,
       doc,
       nfEnt,
       tipo,
       CAST(IF(vencimento * 1 = 0, NULL, vencimento * 1) AS DATE) AS vencimento,
       qtde,
       saldo,
       userLogin,
       observacao
FROM
  sqldados.produtoKardec
WHERE (loja = :loja OR :loja = 0)
  AND prdno = :prdno
  AND grade = :grade
  AND data <= CURRENT_DATE * 1