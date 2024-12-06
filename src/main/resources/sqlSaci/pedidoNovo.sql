DO @LOJA := MID(:ordno, 1, 1) * 1;

SELECT MAX(no + 1) AS ordno
FROM sqldados.ords
WHERE storeno = 1
  AND no >= RPAD(@LOJA, 9, '0') * 1
  AND no <= (RPAD(@LOJA, 9, '9') * 1 + 1) * 1