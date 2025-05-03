DELETE
FROM
  sqldados.iprdAdicionalDev
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
  AND numero = :numero
  AND tipoDevolucao = :tipoDevolucao
