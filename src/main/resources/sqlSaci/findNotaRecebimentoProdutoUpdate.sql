USE sqldados;

SET SQL_MODE = '';

REPLACE sqldados.iprdAdicional(invno, prdno, grade, marcaRecebimento, login, vencimento)
VALUES (:ni, :prdno, :grade, :marca, :login, :vencimento)

