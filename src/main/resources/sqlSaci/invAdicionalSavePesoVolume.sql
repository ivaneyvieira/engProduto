DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV
SELECT invno, tipoDevolucao, numero, volume, peso
FROM
  sqldados.invAdicional
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao;

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, volume, peso)
SELECT invno, tipoDevolucao, numero, :volume, :peso
FROM
  T_INV;

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, volume, peso)
SELECT :invno, :tipoDevolucao, 0, :volume, :peso
FROM
  DUAL
WHERE NOT EXISTS( SELECT * FROM T_INV )
