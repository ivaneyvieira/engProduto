USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT no AS prdno, IFNULL(B.grade, '') AS grade, IF(MID(grade_l, 1, 10) = 0, 'N', 'S') AS temGrade, grade_l
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno
GROUP BY P.no, IFNULL(B.grade, '');

DROP TEMPORARY TABLE IF EXISTS T_PRD_GRADE;
CREATE TEMPORARY TABLE T_PRD_GRADE
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade
FROM
  T_PRD
GROUP BY prdno, grade;


DROP TEMPORARY TABLE IF EXISTS T_PRD_LOC;
CREATE TEMPORARY TABLE T_PRD_LOC
(
  PRIMARY KEY (prdno, grade, storeno, localizacao)
)
SELECT prdno, grade, S.no AS storeno, CONCAT('CD', S.sname) AS localizacao
FROM
  T_PRD_GRADE                 AS G
    INNER JOIN sqldados.store AS S
               ON S.no IN (2, 3, 5, 8)
GROUP BY prdno, grade, storeno, localizacao
ORDER BY prdno, grade, storeno, localizacao;

/*
UPDATE prdAdicional AS A
  INNER JOIN T_PRD_LOC AS L
  ON L.storeno = A.storeno AND L.prdno = A.prdno AND L.grade = A.grade AND A.localizacao = 'CD'
SET A.localizacao = L.localizacao
WHERE A.localizacao = 'CD'
  AND A.storeno != 4;
*/

INSERT IGNORE INTO prdAdicional(storeno, prdno, grade, localizacao)
select storeno, prdno, grade, localizacao
from T_PRD_LOC AS L
where storeno != 4;



REPLACE INTO sqldados.prdloc(stkmin, stkmax, storeno, bits, prdno, localizacao, grade)
SELECT 0 AS stkmin, 0 AS stkmax, storeno, 0 AS bits, prdno, localizacao, grade
FROM
  T_PRD_LOC;

REPLACE INTO sqldados.prdloc2(stkmin, stkmax, l1, l2, l3, l4, l5, l6, l7, l8, m1, m2, m3, m4, m5, m6, m7, m8,
                              storeno, sano, bits, s1, s2, s3, s4, s5, s6, s7, prdno, grade, localizacao, c1, c2)
SELECT 0  AS stkmin,
       0  AS stkmax,
       0  AS l1,
       0  AS l2,
       0  AS l3,
       0  AS l4,
       0  AS l5,
       0  AS l6,
       0  AS l7,
       0  AS l8,
       0  AS m1,
       0  AS m2,
       0  AS m3,
       0  AS m4,
       0  AS m5,
       0  AS m6,
       0  AS m7,
       0  AS m8,
       storeno,
       0  AS sano,
       0  AS bits,
       0  AS s1,
       0  AS s2,
       0  AS s3,
       0  AS s4,
       0  AS s5,
       0  AS s6,
       0  AS s7,
       prdno,
       grade,
       localizacao,
       '' AS c1,
       '' AS c2
FROM
  T_PRD_LOC                    AS L
    LEFT JOIN sqldados.prdloc2 AS L2
              USING (prdno, grade, storeno, localizacao)
WHERE L2.prdno IS NULL
GROUP BY L.prdno, L.grade, L.storeno, L.localizacao;



