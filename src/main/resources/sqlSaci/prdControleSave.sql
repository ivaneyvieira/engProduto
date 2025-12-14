USE sqldados;

REPLACE prdControle(storeno, prdno, grade, dataInicial, estoqueLoja, kardexLoja)
VALUES (:storeno, :prdno, :grade, :dataInicial, :estoqueLoja, :kardexLoja)
