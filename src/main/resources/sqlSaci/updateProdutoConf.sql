UPDATE sqldados.iprdConferencia
SET s27 = :marca
WHERE invno = :invno
  AND prdno = LPAD(:codigo, 16, ' ')
  AND grade = :grade