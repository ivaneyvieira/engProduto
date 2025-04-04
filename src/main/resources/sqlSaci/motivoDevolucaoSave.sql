UPDATE sqldados.iprdAdicional
SET tipoDevolucao = :tipoDevolucao
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade