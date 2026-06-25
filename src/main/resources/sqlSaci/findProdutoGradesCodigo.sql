SELECT TRIM(no)                             AS codigo,
       no                                   AS prdno,
       TRIM(MID(P.name, 1, 37))             AS descricao,
       IFNULL(B.grade, '')                  AS grade,
       0                                    AS saldo,
       TRIM(COALESCE(B.barcode, P.barcode)) AS codigoBarras
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno AND TRIM(B.barcode) != ''
WHERE TRIM(P.no) = :codigo


