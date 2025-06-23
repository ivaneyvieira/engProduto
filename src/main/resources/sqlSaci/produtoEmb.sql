SELECT no  AS prdno,
       CASE
         WHEN P.name LIKE 'SVS E-COLOR%' THEN
           5800.00
         WHEN P.name LIKE 'VRC COLOR%'   THEN
           1000.00
                                         ELSE
           (P.qttyPackClosed / 1000)
       END AS qtdEmbalagem
FROM
  sqldados.prd AS P
HAVING qtdEmbalagem > 0
   AND qtdEmbalagem != 1