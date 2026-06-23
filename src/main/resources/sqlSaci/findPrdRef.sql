USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_REF_PRD;
CREATE TEMPORARY TABLE T_REF_PRD
SELECT prdno                                                    AS prdno,
       TRIM(prdno)                                              AS codigo,
       grade,
       SUBSTRING_INDEX(MAX(CONCAT(l1, ',', prdrefno)), ',', -1) AS ref
FROM sqldados.prdref AS R
WHERE prdrefno = :ref
GROUP BY prdno, grade;

SELECT DISTINCT prdno, codigo, grade, ref
FROM T_REF_PRD
ORDER BY grade
