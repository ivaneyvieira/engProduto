UPDATE sqldados.iprdAdicionalDev
SET seq = :seq
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero