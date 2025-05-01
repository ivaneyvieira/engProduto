DROP TEMPORARY TABLE IF EXISTS T_NI;
CREATE TEMPORARY TABLE T_NI
SELECT invno
FROM
  sqldados.invAdicional
WHERE situacaoDev = :situacaoDev
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

DELETE
FROM
  sqldados.iprdAdicionalDev
WHERE invno IN ( SELECT invno FROM T_NI )
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

DELETE
FROM
  sqldados.invAdicional
WHERE invno IN ( SELECT invno FROM T_NI )
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

DELETE
FROM
  sqldados.invAdicionalDevArquivo
WHERE invno IN ( SELECT invno FROM T_NI )
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero