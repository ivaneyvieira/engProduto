SELECT E.storeno            AS loja,
       E.ordno              AS pedido,
       CAST(E.date AS DATE) AS data,
       P.prdno              AS prdno,
       P.grade              AS grade,
       ROUND(CASE
               WHEN R.remarks__480 LIKE 'ENTRADA%' THEN P.qtty / 1000
               WHEN R.remarks__480 LIKE 'SAIDA%' THEN -P.qtty / 1000
               ELSE 0.00
             END)           AS quantidade
FROM sqldados.eord AS E
       INNER JOIN sqldados.eoprd AS P
                  USING (storeno, ordno)
       LEFT JOIN sqldados.eordrk AS R
                 USING (storeno, ordno)
WHERE E.paymno = 430
  AND E.storeno = :loja
  AND E.date >= :dataInicial
  AND P.prdno = LPAD(:codigo, 16, ' ')
  AND P.grade = :grade
  AND E.status = 4