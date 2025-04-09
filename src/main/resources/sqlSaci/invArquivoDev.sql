SET SQL_MODE = '';

SELECT invno, numero, tipoDevolucao, seq, CAST(date AS DATE) AS date, filename, file
FROM
  sqldados.invAdicionalDevArquivo
WHERE invno = :invno
  AND numero = :numero
  AND tipoDevolucao = :tipo