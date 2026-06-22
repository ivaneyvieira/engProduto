SELECT no AS prdno, TRIM(no) AS codigo, mfno_ref AS ref
FROM sqldados.prd
WHERE mfno_ref = :ref