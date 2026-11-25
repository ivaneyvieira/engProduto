INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, localizacao) VALUE (:loja, :prdno, :grade, :locApp);

UPDATE sqldados.prdAdicional
SET localizacao = :locApp
WHERE storeno = :loja
  AND prdno = :prdno
  AND grade = :prdno