UPDATE sqldados.iprdConferencia
SET marca  = :marca,
    qtty = :qtty * 1000
WHERE nfekey = :nfekey
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade