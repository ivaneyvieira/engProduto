SELECT invno, numero, tipoDevolucao, seq, date, filename, file
FROM
  sqldados.iprdAdicionalDevArquivo
WHERE invno = :invno
  AND numero = :numero
  AND tipoDevolucao = :tipoDevolucao