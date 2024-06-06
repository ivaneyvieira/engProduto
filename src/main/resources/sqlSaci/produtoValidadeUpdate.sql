/*edit header*/
REPLACE INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque, compras)
SELECT :storeno,
       :prdno,
       :grade,
       :dataEntrada,
       :vencimentoEdit AS vencimento,
       :estoque,
       :compras
FROM dual
WHERE :vencimentoEdit < 10 && :vencimento < 10 && :dataEntrada != 0;

/* add by header*/
REPLACE INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque, compras)
SELECT :storeno,
       :prdno,
       :grade,
       :dataEntrada,
       :vencimento,
       :estoque,
       0 AS compras
FROM dual
WHERE :vencimentoEdit < 10 && :vencimento >= 10 && :dataEntrada != 0;

/* Outros casos */
UPDATE sqldados.produtoValidade
SET dataEntrada = :dataEntrada,
    vencimento  = :vencimento,
    estoque     = :estoque,
    compras     = :compras
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND vencimento = :vencimentoEdit
  AND :vencimentoEdit >= 10
