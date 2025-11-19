USE sqldados;

REPLACE xaprd2Devolucao(storeno, pdvno, xano, prdno, grade, quantDev, temProduto)
values(:storeno, :pdvno, :xano, :prdno, :grade, :quantDev, :temProduto)