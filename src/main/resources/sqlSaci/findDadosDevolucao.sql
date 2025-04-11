SELECT invno, numero, tipoDevolucao
FROM
  sqldados.iprdAdicionalDev
WHERE numero = :numero
GROUP BY numero, invno, tipoDevolucao