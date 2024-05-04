USE sqldados;

SET SQL_MODE = '';

REPLACE sqldados.iprdAdicional(invno, prdno, grade, marcaRecebimento, login)
VALUES(:ni, :prdno, :grade, :marca, :login)

