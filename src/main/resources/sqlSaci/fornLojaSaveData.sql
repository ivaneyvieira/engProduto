INSERT IGNORE sqldados.vendLojaAdicional(vendno, storeno, data)
  VALUE (:vendno, :storeno, :data);

UPDATE sqldados.vendLojaAdicional
SET data = :data
WHERE vendno = :vendno
  AND storeno = :storeno