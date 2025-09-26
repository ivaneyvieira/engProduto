DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV
SELECT invno, tipoDevolucao, numero, volume, peso, empTermo, protocolo
FROM
  sqldados.invAdicional
WHERE invno = :invno
  AND tipoDevolucao = :tipoDevolucao;

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, volume, peso, empTermo, empEnvio, empReceb, protocolo)
SELECT invno, tipoDevolucao, numero, :volume, :peso, :empTermo, :empEnvio, :empReceb, :protocolo
FROM
  T_INV;

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, volume, peso, empTermo, empEnvio, empReceb, protocolo)
SELECT :invno, :tipoDevolucao, 0, :volume, :peso, :empTermo, :empEnvio, :empReceb, :protocolo
FROM
  DUAL
WHERE NOT EXISTS( SELECT * FROM T_INV )
