USE sqldados;

SELECT seq, storeno AS loja, pdvno, xano, CAST(IF(date = 0, NULL, date) AS date) AS date, filename, file
FROM
  nfSaidaArquivoDevolucao
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano


