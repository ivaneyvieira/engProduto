DROP TEMPORARY TABLE IF EXISTS T_VAL;
CREATE TEMPORARY TABLE T_VAL
SELECT *
FROM
  sqldados.produtoValidade
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND vencimento = :vencimentoEdit
  AND tipo = :tipoEdit
  AND dataEntrada = :dataEntradaEdit
  AND tipo IN ('INV');

UPDATE sqldados.produtoValidade
SET vencimento  = :vencimento,
    tipo        = :tipo,
    dataEntrada = :dataEntrada,
    movimento   = :movimento
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND vencimento = :vencimentoEdit
  AND tipo = :tipoEdit
  AND dataEntrada = :dataEntradaEdit;

REPLACE INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, movimento, tipo)
SELECT :storeno, :prdno, :grade, :dataEntrada, :vencimento, :movimento, :tipo
FROM
  dual
WHERE NOT EXISTS( SELECT * FROM T_VAL )
