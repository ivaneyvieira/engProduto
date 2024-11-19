SELECT I.storeno                          AS loja,
       I.ordno                            AS pedido,
       I.prdno                            AS prdno,
       I.grade                            AS grade,
       CAST(O.date AS DATE)               AS data,
       TRIM(I.prdno) * 1                  AS codigo,
       TRIM(MID(P.name, 1, 37))           AS descricao,
       IFNULL(R.prdrefno, P.mfno_ref)     AS refFor,
       TRIM(IFNULL(B.barcode, P.barcode)) AS barcode,
       TRIM(MID(P.name, 37, 3))           AS unidade,
       I.qtty                             AS quant,
       IFNULL(PN.quantFat, I.qtty)        AS quantFat,
       I.cost                             AS valorUnit,
       P.mult / 1000                      AS embalagem,
       IF(P.free_fld1 LIKE '*%' ||
          P.free_fld1 LIKE '/%',
          P.free_fld1, NULL)              AS formula
FROM sqldados.ords AS O
       INNER JOIN sqldados.oprd AS I
                  ON I.storeno = O.storeno
                    AND I.ordno = O.no
       LEFT JOIN sqldados.pedidoPrdNdd AS PN
                 ON I.storeno = PN.storeno
                   AND I.ordno = PN.ordno
                   AND I.prdno = PN.prdno
                   AND I.grade = PN.grade
       INNER JOIN sqldados.prd AS P
                  ON P.no = I.prdno
       LEFT JOIN sqldados.prdbar AS B
                 ON I.prdno = B.prdno
                   AND I.grade = B.grade
                   AND B.grade != ''
       LEFT JOIN sqldados.prdref AS R
                 ON R.prdno = I.prdno
                   AND R.grade = I.grade
                   AND R.vendno = O.vendno
WHERE (I.storeno = :loja
  AND I.ordno = :pedido)
   OR (I.storeno = :loja
  AND O.vendno = :vendno
  AND :vendno != 0
  AND O.remarks LIKE CONCAT('%NFO%', :numero, '%')
  )