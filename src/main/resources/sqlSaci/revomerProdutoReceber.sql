DELETE
FROM sqldados.iprdConferencia
WHERE nfekey = :nfekey
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade