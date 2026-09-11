UPDATE IGNORE sqldados.iprdAdicionalDev
SET numAcerto  = :numAcerto,
    dataAcerto = :dataAcerto
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero
  AND seq = :seq