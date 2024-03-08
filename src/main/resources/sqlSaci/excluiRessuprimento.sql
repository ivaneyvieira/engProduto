DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT S.prdno                                               AS prdno,
       S.grade                                               AS grade,
       COALESCE(A.localizacao, MID(L.localizacao, 1, 4), '') AS localizacao
FROM sqldados.stk AS S
       LEFT JOIN sqldados.prdloc AS L
                 ON S.storeno = L.storeno
                   AND S.prdno = L.prdno
                   AND S.grade = L.grade
       LEFT JOIN sqldados.prdAdicional AS A
                 ON S.storeno = A.storeno
                   AND S.prdno = A.prdno
                   AND S.grade = A.grade
                   AND A.localizacao != ''
WHERE S.storeno = 4
  AND COALESCE(A.localizacao, MID(L.localizacao, 1, 4), '') = :localizacao
GROUP BY S.storeno, S.prdno, S.grade;

UPDATE sqldados.oprd AS O
  INNER JOIN T_LOC AS L
  USING (prdno, grade)
SET O.auxShort4 = 999
WHERE O.storeno = :storeno
  AND O.ordno = :ordno
  AND O.auxShort4 = :marca;

UPDATE sqldados.oprdRessu AS O
  INNER JOIN T_LOC AS L
  USING (prdno, grade)
SET O.auxShort4 = 999
WHERE O.storeno = :storeno
  AND O.ordno = :ordno
  AND O.auxShort4 = :marca

