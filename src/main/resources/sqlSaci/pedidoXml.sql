SELECT I.storeno                          AS loja,
       I.ordno                            AS pedido,
       I.prdno                            AS prdno,
       I.grade                            AS grade,
       TRIM(I.prdno) * 1                  AS codigo,
       TRIM(MID(P.name, 1, 37))           AS descricao,
       P.mfno_ref                         AS refFor,
       TRIM(IFNULL(B.barcode, P.barcode)) AS barcode,
       TRIM(MID(P.name, 37, 3))           AS unidade,
       I.qtty                             AS quant,
       I.cost                             AS valorUnit
FROM sqldados.ords AS O
       INNER JOIN sqldados.oprd AS I
                  ON I.storeno = O.storeno
                    AND I.ordno = O.no
       INNER JOIN sqldados.prd AS P
                  ON P.no = I.prdno
       LEFT JOIN sqldados.prdbar AS B
                 ON I.prdno = B.prdno
                   AND I.grade = B.grade
WHERE I.storeno = :loja
  AND I.ordno = :pedido
GROUP BY I.storeno, I.ordno, I.prdno, I.grade