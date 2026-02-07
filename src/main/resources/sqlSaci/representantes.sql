SELECT RV.vendno                                                                    AS vendno,
       CAST(CONCAT(R.no, ' ', R.name) AS CHAR)                                      AS nome,
       CAST(CONCAT(TRIM(MID(R.ddd, 1, 5)), ' - ', TRIM(MID(R.phone, 1, 10))) AS CHAR) AS telefone,
       IF(R.celular = 0, '', CAST(R.celular AS CHAR))                               AS celular,
       R.email                                                                      AS email
FROM
  sqldados.repven          AS RV
    LEFT JOIN sqldados.rep AS R
              ON R.no = RV.repno
WHERE RV.vendno = :vendno