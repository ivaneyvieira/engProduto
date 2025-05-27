SELECT no                    AS carrno,
       name                  AS nome,
       addr,
       insc,
       cgc,
       nei,
       city,
       state,
       zip,
       TRIM(MID(ddd, 1, 5))  AS ddd,
       TRIM(MID(tel, 1, 10)) AS tel
FROM
  sqldados.carr
WHERE no = :carrno