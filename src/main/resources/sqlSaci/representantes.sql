USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_REP;
CREATE TEMPORARY TABLE T_REP
(
  INDEX (repno)
)
SELECT R.no                                                                         AS repno,
       RV.vendno                                                                    AS vendno,
       CAST(CONCAT(R.no, ' - ', R.name) AS CHAR)                                    AS nome,
       CAST(CONCAT(TRIM(MID(R.ddd, 1, 5)), ' ', TRIM(MID(R.phone, 1, 10))) AS CHAR) AS telefone,
       IF(R.celular = 0, '', CAST(R.celular AS CHAR))                               AS celular,
       R.email                                                                      AS email,
       R.ddd                                                                        AS ddd,
       phone                                                                        AS phone,
       obs_tel1                                                                     AS obs_tel1,
       obs_tel2                                                                     AS obs_tel2,
       obs_tel3                                                                     AS obs_tel3,
       obs_tel4                                                                     AS obs_tel4
FROM
  sqldados.repven          AS RV
    LEFT JOIN sqldados.rep AS R
              ON R.no = RV.repno
WHERE RV.vendno = :vendno;

DROP TEMPORARY TABLE IF EXISTS T_REP_PHONE;
CREATE TEMPORARY TABLE T_REP_PHONE
(
  INDEX (repno)
)
SELECT 1 AS numPhone, repno, TRIM(MID(ddd, 1, 5)) AS ddd, TRIM(MID(phone, 1, 10)) AS phone, obs_tel1 AS obs_tel
FROM
  T_REP;

INSERT INTO T_REP_PHONE(numPhone, repno, ddd, phone, obs_tel)
SELECT 2 AS numPhone, repno, TRIM(MID(ddd, 6, 5)) AS ddd, TRIM(MID(phone, 11, 10)) AS phone, obs_tel2 AS obs_tel
FROM
  T_REP;

INSERT INTO T_REP_PHONE(numPhone, repno, ddd, phone, obs_tel)
SELECT 3 AS numPhone, repno, TRIM(MID(ddd, 11, 5)) AS ddd, TRIM(MID(phone, 21, 10)) AS phone, obs_tel3 AS obs_tel
FROM
  T_REP;

INSERT INTO T_REP_PHONE(numPhone, repno, ddd, phone, obs_tel)
SELECT 4 AS numPhone, repno, TRIM(MID(ddd, 16, 5)) AS ddd, TRIM(MID(phone, 31, 10)) AS phone, obs_tel4 AS obs_tel
FROM
  T_REP;


SELECT vendno,
       repno,
       nome,
       numPhone,
       CONCAT(P.ddd, ' ', P.phone) AS telefone,
       P.obs_tel                   AS obsTel,
       email                       AS email,
       celular                     AS celular
FROM
  T_REP                    AS R
    INNER JOIN T_REP_PHONE AS P
               USING (repno)
WHERE P.phone > 0
ORDER BY vendno, repno, numPhone