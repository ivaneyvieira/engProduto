SELECT E.storeno            AS loja,
       E.ordno              AS pedido,
       CAST(E.date AS DATE) AS data,
       P.prdno              AS prdno,
       P.grade              AS grade,
       P.qtty / 1000        AS quantidade
FROM sqldados.eord AS E
       INNER JOIN sqldados.eoprd AS P
                  USING (storeno, ordno)
WHERE E.paymno = 430
  AND E.storeno = :loja
  AND E.date >= :dataInicial
  AND P.prdno = LPAD(:codigo, 16, ' ')
  AND P.grade = :grade