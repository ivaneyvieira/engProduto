USE sqldados;

SELECT seq, storeno AS loja, pdvno, xano, CAST(IF(date = 0, NULL, date) AS date) AS date, 'S' AS tipo, filename, file
FROM
  nfSaidaArquivoDevolucao
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
