SELECT TRIM(no)                 AS codigo,
       no                       AS prdno,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       ''                       AS grade,
       0                        AS saldo
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prd2   AS P2
              ON P.no = P2.prdno
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno
                AND TRIM(B.barcode) != ''
WHERE (TRIM(P.barcode) = :codigo
  OR TRIM(no) = :codigo
  OR TRIM(P2.gtin) = :codigo)
  AND B.prdno IS NULL
UNION
SELECT TRIM(no)                 AS codigo,
       no                       AS prdno,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       B.grade                  AS grade,
       0                        AS saldo
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno
                AND TRIM(B.barcode) != ''
WHERE (TRIM(B.barcode) = :codigo
  OR TRIM(P.no) = :codigo)
  AND B.prdno IS NOT NULL