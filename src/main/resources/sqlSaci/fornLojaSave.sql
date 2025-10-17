REPLACE sqldados.vendLojaAdicional(vendno, storeno, data)
  VALUE (:vendno, :storeno, :data);

DELETE
FROM
  sqldados.vendLojaAdicional
WHERE :data = 0