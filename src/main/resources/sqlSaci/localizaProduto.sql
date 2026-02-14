USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT no                                                                    AS prdno,
       TRIM(MID(name, 1, 37))                                                AS descricao,
       IFNULL(B.grade, '')                                                   AS grade,
       MAX(TRIM(IF(B.grade IS NULL, IFNULL(P2.gtin, P.barcode), B.barcode))) AS barcode,
       mfno                                                                  AS codForn
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prd2   AS P2
              ON P.no = P2.prdno
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno
                AND TRIM(B.barcode) != TRIM(P.no)
WHERE (P.mfno = :codForn OR :codForn = 0)
  AND (P.typeno = :tipo OR :tipo = 0)
  AND (P.clno = :cl OR :cl = 0)
  AND (P.name LIKE CONCAT(:pesquisa, '%') OR :pesquisa = '')
GROUP BY P.no, IFNULL(B.grade, '')
HAVING (barcode = :barcode OR :barcode = '');

DROP TEMPORARY TABLE IF EXISTS T_LOC_APP;
CREATE TEMPORARY TABLE T_LOC_APP
(
  PRIMARY KEY (prdno, grade)
)
SELECT P.storeno,
       P.prdno,
       P.grade,
       MID(P.localizacao, 1, 4)                       AS locApp,
       ROUND((S.qtty_atacado + S.qtty_varejo) / 1000) AS estoqueLoja
FROM
  sqldados.prdAdicional     AS P
    INNER JOIN T_PRD        AS A
               ON P.prdno = A.prdno
                 AND P.grade = A.grade
    LEFT JOIN  sqldados.stk AS S
               ON S.storeno = P.storeno
                 AND S.prdno = P.prdno
                 AND S.grade = P.grade
WHERE P.storeno = 4
GROUP BY P.prdno, P.grade;

DROP TEMPORARY TABLE IF EXISTS T_RESULT;
CREATE TEMPORARY TABLE T_RESULT
SELECT :loja AS loja,
       P.prdno,
       P.grade,
       P.barcode,
       P.descricao,
       P.codForn,
       L.locApp,
       L.estoqueLoja
FROM
  T_PRD                 AS P
    LEFT JOIN T_LOC_APP AS L
              ON P.prdno = L.prdno
                AND P.grade = L.grade;

SELECT loja,
       prdno,
       barcode,
       grade,
       descricao,
       codForn,
       locApp,
       estoqueLoja
FROM
  T_RESULT