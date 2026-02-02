UPDATE IGNORE sqldados.iprdAdicionalDev
SET numAcerto = :numAcerto
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero