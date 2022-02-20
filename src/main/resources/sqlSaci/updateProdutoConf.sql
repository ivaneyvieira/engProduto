UPDATE sqldados.iprdConferencia
SET s27  = :marca,
    qtty = :qtty * 1000
WHERE invno = :invno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade