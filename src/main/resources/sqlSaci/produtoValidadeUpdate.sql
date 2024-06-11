UPDATE sqldados.produtoValidade
SET vencimento  = :vencimento,
    tipo        = :tipo,
    dataEntrada = :dataEntrada,
    movimento   = :movimento
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND vencimento = :vencimentoEdit
  AND tipo = :tipoEdit;

REPLACE INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, movimento, tipo)
SELECT :storeno,
       :prdno,
       :grade,
       :dataEntrada,
       :vencimento,
       :movimento,
       :tipo
FROM dual
