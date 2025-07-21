SELECT seq, vendno, filename, file
FROM
  sqldados.vendArquivo
WHERE vendno = :vendno
ORDER BY seq