SELECT *
FROM sqldados.prdloc
WHERE storeno = 3;

SELECT *
FROM sqldados.prdloc2
WHERE storeno = 3;

SHOW INDEX FROM sqldados.prdloc2;

SHOW INDEX FROM sqldados.prdloc;


DROP TEMPORARY TABLE IF EXISTS T_PRD_GRADE3;
CREATE TEMPORARY TABLE T_PRD_GRADE3
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno, prdno, grade, qtty_varejo
FROM stk AS S
       INNER JOIN prd AS P
                  ON P.no = S.prdno
WHERE storeno = 3;

DELETE
FROM prdloc
WHERE storeno = 3
  AND localizacao = 'EXP3';

INSERT INTO sqldados.prdloc(stkmin, stkmax, storeno, bits, prdno, localizacao, grade)
SELECT 0 AS stkmin, 0 AS stkmax, storeno, 0 AS bits, prdno, 'EXP3' AS localizacao, grade
FROM T_PRD_GRADE3;

DELETE
FROM prdloc2
WHERE storeno = 3
  AND localizacao = 'EXP3';


INSERT INTO sqldados.prdloc2(stkmin, stkmax, l1, l2, l3, l4, l5, l6, l7, l8, m1, m2, m3, m4, m5, m6, m7, m8,
                             storeno, sano, bits, s1, s2, s3, s4, s5, s6, s7, prdno, grade, localizacao, c1, c2)
SELECT 0      AS stkmin,
       0      AS stkmax,
       0      AS l1,
       0      AS l2,
       0      AS l3,
       0      AS l4,
       0      AS l5,
       0      AS l6,
       0      AS l7,
       0      AS l8,
       0      AS m1,
       0      AS m2,
       0      AS m3,
       0      AS m4,
       0      AS m5,
       0      AS m6,
       0      AS m7,
       0      AS m8,
       storeno,
       0      AS sano,
       0      AS bits,
       0      AS s1,
       0      AS s2,
       0      AS s3,
       0      AS s4,
       0      AS s5,
       0      AS s6,
       0      AS s7,
       prdno,
       grade,
       'EXP3' AS localizacao,
       ''     AS c1,
       ''     AS c2
FROM T_PRD_GRADE3
