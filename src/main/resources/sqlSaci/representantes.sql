USE sqldados;

DO @NO := 1;

DROP TEMPORARY TABLE IF EXISTS T_REP;
CREATE TEMPORARY TABLE T_REP
(
  INDEX (repno)
)
SELECT @NO := @NO + 1                                                         AS repno,
       vendno                                                                 AS vendno,
       name                                                                   AS nome,
       CAST(CONCAT(TRIM(MID(ddd, 1, 5)), ' ', TRIM(MID(tel, 1, 10))) AS CHAR) AS telefone,
       IF(celular = 0, '', CAST(celular AS CHAR))                             AS celular,
       ddd                                                                    AS ddd,
       tel                                                                    AS phone,
       remarks                                                                AS obs_tel
FROM sqldados.vendct
WHERE vendno = :vendno;

DROP TEMPORARY TABLE IF EXISTS T_REP_PHONE;
CREATE TEMPORARY TABLE T_REP_PHONE
(
  INDEX (repno)
)
SELECT 1 AS numPhone, repno, TRIM(MID(ddd, 1, 5)) AS ddd, TRIM(MID(phone, 1, 10)) AS phone, obs_tel AS obs_tel
FROM T_REP
WHERE phone > 0;

DROP TABLE IF EXISTS T_EMAIL_UNION;
CREATE TEMPORARY TABLE T_EMAIL_UNION
(
  PRIMARY KEY (vendno)
)
SELECT vendno, GROUP_CONCAT(DISTINCT email) AS emailList
FROM sqldados.vendct
GROUP BY vendno;

SELECT vendno,
       0                     AS repno,
       name                  AS nome,
       tel                   AS numPhone,
       CONCAT(ddd, ' ', tel) AS telefone,
       remarks               AS obsTel,
       email                 AS email,
       celular               AS celular
FROM sqldados.vendct
WHERE vendno = :vendno
ORDER BY vendno, repno, numPhone