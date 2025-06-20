SELECT no, name, addr, insc, cgc, nei, city, state, zip, TRIM(MID(ddd, 1, 5)) AS ddd, TRIM(MID(tel, 1, 10)) AS tel
FROM
  sqldados.vend
WHERE no = :vendno