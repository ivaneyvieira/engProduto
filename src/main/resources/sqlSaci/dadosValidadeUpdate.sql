UPDATE sqldados.dadosValidade
SET vencimento  = :vencimento,
    inventario  = :inventario,
    dataEntrada = :dataEntrada
WHERE seq = :seq
  AND :seq > 0;

INSERT INTO sqldados.dadosValidade(storeno, prdno, grade, vencimento, inventario, dataEntrada)
SELECT :storeno, :prdno, :grade, :vencimento, :inventario, :dataEntrada
FROM
  dual
WHERE :seq = 0

