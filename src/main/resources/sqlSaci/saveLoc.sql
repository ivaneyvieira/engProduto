INSERT IGNORE sqldados.prdAdicional(storeno, prdno, grade, localizacao) VALUE (:loja, :prdno, :grade, :locApp);

UPDATE sqldados.prdAdicional
set localizacao = :locApp
where storeno = :loja
and prdno = :prdno
and grade = :prdno