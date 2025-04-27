DELETE
FROM
  sqldados.iprdAdicionalDev
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;


DELETE
FROM
  sqldados.invAdicional
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

DELETE
FROM
  sqldados.invAdicionalDevArquivo
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero