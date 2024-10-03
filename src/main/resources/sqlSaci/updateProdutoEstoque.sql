/*
DELETE
FROM sqldados.prdAdicional
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :grade
  AND localizacao = :locApp*/

REPLACE INTO sqldados.prdAdicional(storeno, prdno, grade, estoque, localizacao, dataInicial)
  VALUE (:loja, :prdno, :grade, :estoque, :locApp, :dataInicial)
