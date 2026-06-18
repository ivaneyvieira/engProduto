USE sqldados;

REPLACE xaprd2Devolucao(storeno, pdvno, xano, prdno, grade, seq, quantDev, temProduto, dev)
VALUES (:storeno, :pdvno, :xano, :prdno, :grade, IFNULL(:seq, 1), :quantDev, :temProduto, :dev);

DELETE
FROM xaprd2Devolucao
WHERE dev = 0
  AND storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND prdno = :prdno
  AND grade = :grade


