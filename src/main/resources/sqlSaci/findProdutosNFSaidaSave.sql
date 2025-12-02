USE sqldados;

DO @SEQ := IF(:seq = 0, ( SELECT IFNULL(MAX(seq), 0) + 1
                          FROM
                            xaprd2Devolucao
                          WHERE storeno = :storeno
                            AND pdvno = :pdvno
                            AND xano = :xano
                            AND prdno = :prdno
                            AND grade = :grade ), :seq);

REPLACE xaprd2Devolucao(storeno, pdvno, xano, prdno, grade, seq, quantDev, temProduto, dev)
VALUES (:storeno, :pdvno, :xano, :prdno, :grade, @SEQ, :quantDev, :temProduto, :dev);

DELETE
FROM
  xaprd2Devolucao
WHERE dev = 0
  AND storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND prdno = :prdno
  AND grade = :grade


