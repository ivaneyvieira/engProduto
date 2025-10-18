INSERT IGNORE sqldados.vendLojaAdicional(vendno, storeno, userno)
  VALUE (:vendno, :storeno, :userno);

UPDATE sqldados.vendLojaAdicional
SET userno = :userno
WHERE vendno = :vendno
  AND storeno = :storeno