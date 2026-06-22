SELECT no AS prdno, TRIM(no) AS codigo, mfno_ref AS ref
FROM sqldados.prd
WHERE no = LPAD(:cod, 16, ' ')