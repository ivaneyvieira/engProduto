INSERT IGNORE sqldados.invAdicional(invno, tipoDevolucao, numero)
SELECT invno, :tipoDevolucaoNovo, numero
FROM
  sqldados.iprdAdicionalDev
WHERE tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE IGNORE
  sqldados.invAdicional
SET tipoDevolucao = :tipoDevolucaoNovo
WHERE situacaoDev = :situacaoDev
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE IGNORE
  sqldados.iprdAdicionalDev
SET tipoDevolucao = :tipoDevolucaoNovo
WHERE tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE IGNORE
  sqldados.invAdicionalDevArquivo
SET tipoDevolucao = :tipoDevolucaoNovo
WHERE tipoDevolucao = :tipoDevolucao
  AND numero = :numero
