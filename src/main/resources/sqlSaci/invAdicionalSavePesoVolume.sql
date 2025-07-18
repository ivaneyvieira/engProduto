DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV
SELECT invno, tipoDevolucao, numero, volume, peso, empTermo
FROM
  sqldados.invAdicional
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao;

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, volume, peso, empTermo)
SELECT invno, tipoDevolucao, numero, :volume, :peso, :empTermo
FROM
  T_INV;

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, volume, peso, empTermo)
SELECT :invno, :tipoDevolucao, 0, :volume, :peso, :empTermo
FROM
  DUAL
WHERE NOT EXISTS( SELECT * FROM T_INV )
