REPLACE sqldados.iprdAdicionalDev(invno, prdno, grade, numero, tipoDevolucao, quantDevolucao)
  VALUE (:invno, :prdno, :grade, :numero, :tipoDevolucao, :quantDevolucao);

UPDATE sqldados.invAdicional
SET situacaoDev = :situacaoDev
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero