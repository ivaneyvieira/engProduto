SET
SQL_MODE = '';

SELECT seq, invno, title, CAST(date AS DATE) AS date, filename, file
FROM sqldados.invAdicionalArquivos
WHERE invno = :invno