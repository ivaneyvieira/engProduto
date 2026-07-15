SELECT DISTINCT TRIM(no)                                      AS codigo,
                no                                            AS prdno,
                TRIM(MID(P.name, 1, 37))                      AS descricao,
                IFNULL(B.grade, '')                           AS grade,
                0                                             AS saldo,
                TRIM(COALESCE(B.barcode, P2.gtin, P.barcode)) AS codigoBarras
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prd2   AS P2
              ON P.no = P2.prdno AND LENGTH(TRIM(P2.gtin)) > 10
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno AND LENGTH(TRIM(B.barcode)) > 10
WHERE P.no <=> LPAD(:codigo, 16, ' ')
