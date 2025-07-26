SELECT no    AS vendno,
       name  AS nome,
       cgc   AS cgc,
       sname AS abrev
FROM
  sqldados.vend
WHERE no = :vendno