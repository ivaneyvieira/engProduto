SELECT invno,
       prdno,
       grade,
       CAST(IF(issue_date = 0, NULL, issue_date) AS date) AS data,
       ROUND(SUM(qtty / 1000))                            AS quantidadeDev
FROM
  sqldados.iprd
    INNER JOIN sqldados.inv
               USING (invno)
WHERE invno = :invno
GROUP BY invno, prdno, grade