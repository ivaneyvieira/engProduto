USE sqldados;

SET SQL_MODE = '';

REPLACE sqldados.iprdAdicional(invno, prdno, grade, marcaRecebimento, login, validade, vencimento)
VALUES(:ni, :prdno, :grade, :marca, :login, :validade, :vencimento)

