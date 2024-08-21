DROP TABLE IF EXISTS T_USER;
CREATE TEMPORARY TABLE IF NOT EXISTS T_USER
(
  PRIMARY KEY (login)
)
SELECT MAX(no) AS userno, login
FROM sqldados.users
WHERE (bits1 & POW(2, 0)) = 0
GROUP BY login;

DROP TABLE IF EXISTS T_NF;
CREATE TEMPORARY TABLE IF NOT EXISTS T_NF
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT storeno, pdvno, xano
FROM sqldados.nf
WHERE storeno = 4
  AND issuedate >= 20240701;

UPDATE sqldados.xaprd2 AS X
  INNER JOIN T_NF
  USING (storeno, pdvno, xano)
  LEFT JOIN T_USER AS T ON T.login = X.c3
SET X.s3 = IFNULL(T.userno, 0)
WHERE X.c3 != '';

UPDATE sqldados.xaprd2 AS X
  INNER JOIN T_NF
  USING (storeno, pdvno, xano)
  LEFT JOIN T_USER AS T ON T.login = SUBSTRING_INDEX(X.c4, '-', 1)
SET X.s4 = IFNULL(T.userno, 0)
WHERE X.c4 != '';

UPDATE sqldados.xaprd2 AS X
  INNER JOIN T_NF
  USING (storeno, pdvno, xano)
  LEFT JOIN T_USER AS T ON T.login = SUBSTRING_INDEX(X.c5, '-', 1)
SET X.s5 = IFNULL(T.userno, 0)
WHERE X.c5 != '';


SELECT storeno,
       pdvno,
       xano,
       s5 AS exp,
       s3 AS sep,
       P.userno,
       P.usernoSingExt,
       P.usernoSing
FROM sqldados.nfUserPrint AS P
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
WHERE usernoSingExt > 0
   OR usernoSing > 0;

UPDATE sqldados.xaprd2 AS X
  INNER JOIN sqldados.nfUserPrint AS P
  USING (storeno, pdvno, xano)
SET X.s3 = P.usernoSing
WHERE X.s3 = 0
  AND P.usernoSing > 0;

UPDATE sqldados.xaprd2 AS X
  INNER JOIN sqldados.nfUserPrint AS P
  USING (storeno, pdvno, xano)
SET X.s3 = P.usernoSingExt
WHERE X.s3 = 0
  AND P.usernoSingExt > 0;
