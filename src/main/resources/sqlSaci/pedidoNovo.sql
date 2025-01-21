DO @LOJA := MID(:ordno, 1, 1) * 1;

DROP TEMPORARY TABLE IF EXISTS T_MAX;
CREATE TEMPORARY TABLE T_MAX
SELECT (MAX(no) + 1) AS ordno
FROM
  sqldados.lastno AS D
WHERE se = 'RS'
  AND storeno = @LOJA
  AND dupse = 0
  AND no >= RPAD(@LOJA, 9, '0') * 1
  AND no <= (RPAD(@LOJA, 9, '9') * 1 + 1) * 1
UNION
SELECT MAX(no + 1) AS ordno
FROM
  sqldados.ords
WHERE storeno = 1
  AND no >= RPAD(@LOJA, 9, '0') * 1
  AND no <= (RPAD(@LOJA, 9, '9') * 1 + 1) * 1;

SELECT MAX(ordno) AS ordno
FROM
  T_MAX