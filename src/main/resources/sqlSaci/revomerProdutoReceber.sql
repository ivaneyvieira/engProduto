DELETE
FROM sqldados.iprdConferencia
WHERE invno = :ni
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade