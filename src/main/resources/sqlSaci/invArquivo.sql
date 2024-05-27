SELECT seq, invno, title, filename, file
FROM sqldados.invAdicionalArquivos
where invno = :invno