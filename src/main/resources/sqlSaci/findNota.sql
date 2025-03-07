SELECT N.nfno               AS numero,
       N.nfse               AS serie,
       CAST(N.date AS DATE) AS data,
       N.custno             AS cliente,
       C.name               AS nomeCliente,
       N.empno              AS vendedor,
       V.name               AS nomeVendedor
FROM
  sqlpdv.pxa                  AS N
    INNER JOIN sqldados.custp AS C
               ON N.custno = C.no
    LEFT JOIN  sqldados.emp   AS V
               ON V.no = N.empno
WHERE N.storeno IN (2, 3, 4, 5, 6, 8)
  AND N.nfno = :nfno
  AND N.nfse = :nfse
  AND N.date BETWEEN SUBDATE(:data, 60) * 1 AND ADDDATE(:data, 2) * 1
ORDER BY N.date DESC
LIMIT 1