USE sqldados;

DELETE
FROM
  produtoMovimentacao
WHERE (numero = 0 AND data < CURDATE())
   OR (noGravado = 0 AND data < CURDATE());

DROP TEMPORARY TABLE IF EXISTS T_NUMERO;
CREATE TEMPORARY TABLE T_NUMERO
(
  PRIMARY KEY (numero)
)
SELECT numero
FROM
  produtoMovimentacao
WHERE numloja = :numLoja
GROUP BY numero;

DROP TEMPORARY TABLE IF EXISTS T_MAX;
CREATE TEMPORARY TABLE T_MAX
SELECT MAX(numero + 1) AS numero
FROM
  T_NUMERO;

DO @NUM := 0;
DO @QUANT := ( SELECT numero
               FROM
                 T_MAX );

DROP TEMPORARY TABLE IF EXISTS T_SEQ;
CREATE TEMPORARY TABLE T_SEQ
(
  PRIMARY KEY (numero)
)
SELECT @NUM := @NUM + 1 AS numero
FROM
  sqldados.nf
WHERE @NUM <= @QUANT;

SELECT MIN(numero) AS quant
FROM
  T_SEQ                AS S
    LEFT JOIN T_NUMERO AS N
              USING (numero)
WHERE N.numero IS NULL


