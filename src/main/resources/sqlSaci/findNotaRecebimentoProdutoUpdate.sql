USE sqldados;

SET SQL_MODE = '';

REPLACE sqldados.iprdAdicional(invno, prdno, grade, marcaRecebimento, login, vencimento, selecionado)
VALUES (:ni, :prdno, :grade, :marca, :login, :vencimento, :selecionado);

UPDATE sqldados.iprd
SET s26 = :usernoRecebe
WHERE invno = :ni
  AND prdno = :prdno
  AND grade = :grade
