SELECT MAX(numero + 1) AS quant
FROM
  sqldados.iprdAdicionalDev
WHERE invno = :ni
  AND tipoDevolucao = :tipo