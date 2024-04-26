USE sqldados;

SET SQL_MODE = '';

REPLACE sqldados.iprdAdicional(invno, prdno, grade, marcaRecebimento)
VALUES(:ni, :prdno, :grade, :marca)
