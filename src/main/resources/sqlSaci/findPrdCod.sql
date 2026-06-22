USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_REF_PRD;
CREATE TEMPORARY TABLE T_REF_PRD SELECT 100 AS ordem, no AS prdno, TRIM(no) AS codigo, NULL AS grade, mfno_ref AS ref
                                 FROM sqldados.prd
                                 WHERE no = LPAD(:cod, 16, ' ')
                                 UNION
                                 SELECT DISTINCT 80             AS ordem,
                                                 prdnoRef       AS prdno,
                                                 TRIM(prdnoRef) AS codigo,
                                                 grade,
                                                 prdno          AS ref
                                 FROM sqldados.mfprd
                                 WHERE prdnoRef = LPAD(:cod, 16, ' ')
                                 UNION
                                 SELECT DISTINCT 50          AS ordem,
                                                 prdno       AS prdno,
                                                 TRIM(prdno) AS codigo,
                                                 grade,
                                                 prdrefno    AS ref
                                 FROM sqldados.prdrefpq
                                 WHERE prdno = LPAD(:cod, 16, ' ');

SELECT prdno, codigo, grade, ref
FROM T_REF_PRD
ORDER BY ordem
