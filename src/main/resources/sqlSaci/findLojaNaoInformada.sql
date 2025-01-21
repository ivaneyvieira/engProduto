SELECT C.no AS codigo, C.name AS nome
FROM
  sqldados.custp              AS C
    INNER JOIN sqldados.store AS S
               ON S.cgc = C.cpf_cgc
WHERE :custno IN (200, 300, 400, 500, 600, 700, 800)
  AND S.no = ROUND(:custno / 100)

