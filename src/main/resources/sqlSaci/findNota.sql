SELECT N.nfno   AS               numero,
       N.nfse   AS               serie,
       CAST(N.issuedate AS DATE) data,
       N.custno AS               cliente,
       C.name   AS               nomeCliente,
       N.empno  AS               vendedor,
       V.name   AS               nomeVendedor
FROM sqldados.nf AS N
       INNER JOIN sqldados.custp AS C
                  ON N.custno = C.no
       LEFT JOIN sqldados.emp AS V
                 ON V.no = N.empno
WHERE N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8)
  AND N.nfno = :nfno
  AND N.nfse = :nfse
  AND N.issuedate <= :data
ORDER BY N.issuedate DESC
LIMIT 1