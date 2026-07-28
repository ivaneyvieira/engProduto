SELECT invno, prdno, grade, ROUND(SUM(qtty / 1000)) AS quantidadeDev
FROM sqldados.iprd
WHERE invno = :invno
GROUP BY invno, prdno, grade