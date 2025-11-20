USE sqldados;

REPLACE xaprd2Devolucao(storeno, pdvno, xano, prdno, grade, quantDev, temProduto, dev)
values(:storeno, :pdvno, :xano, :prdno, :grade, :quantDev, :temProduto, :dev)