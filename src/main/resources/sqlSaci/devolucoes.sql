SELECT invno, prdno, grade, numero, tipoDevolucao, quantDevolucao
FROM
  sqldados.iprdAdicionalDev
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade