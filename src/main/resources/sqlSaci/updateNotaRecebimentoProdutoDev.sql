UPDATE IGNORE sqldados.iprdAdicionalDev
SET seq = :seq
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE IGNORE sqldados.iprdAdicionalDev
SET grade = :gradeNova,
    invno = :invnoNovo
WHERE invno = :invno
  AND prdno = :prdno
  AND grade = :grade
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE IGNORE sqldados.invAdicional
SET situacaoDev = :situacaoDev
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE IGNORE sqldados.invAdicional
SET invno = :invnoNovo
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero
