SET
SQL_MODE = '';

USE
sqldados;

/*storeno, prdno, grade, xano*/


DO
@XANO := IFNULL(@XANO_MOV, -1);

DO
@QUANT_DEL := IFNULL((SELECT qtty
                         FROM sqldados.stkmov
                         WHERE storeno = :storeno
                           AND prdno = :prdno
                           AND grade = :grade
                           AND xano = :xano), 0);

UPDATE sqldados.stk
SET qtty_atacado = qtty_atacado - @QUANT_DEL
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade;

DELETE
FROM sqldados.stkmov
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND xano = :xano