SELECT numero, numloja, data, hora, usuario, prdno, grade, estoqueSis, estoqueReal, loteDev
FROM
  sqldados.produtoEstoqueGarantia
GROUP BY numloja, numero, prdno, grade;

SELECT invno, prdno, grade, numero, tipoDevolucao, quantDevolucao
FROM
  sqldados.iprdAdicionalDev
GROUP BY invno, prdno, grade, tipoDevolucao, numero;

SELECT *
FROM
  sqldados.invAdicional
GROUP BY invno, tipoDevolucao, numero;

SELECT *
FROM
  sqldados.inv
ORDER BY invno;

SELECT *
FROM
  sqldados.nf
ORDER BY issuedate;

/***************************/

USE sqldados;

DO @NUMERO := ( SELECT MAX(quant) AS quant
                FROM
                  ( SELECT MAX(numero + 1) AS quant
                    FROM
                      sqldados.produtoEstoqueAcerto
                    UNION
                    SELECT MAX(numero + 1) AS quant
                    FROM
                      sqldados.produtoEstoqueGarantia ) AS D );

SELECT @NUMERO AS numero;

DO @NUMERO := @NUMERO - 1;

DROP TEMPORARY TABLE IF EXISTS T_NUMERO;
CREATE TEMPORARY TABLE T_NUMERO
(
  PRIMARY KEY (numero)
)
SELECT numero, @NUMERO := @NUMERO + 1 AS novo
FROM
  ( SELECT DISTINCT numero
    FROM
      sqldados.produtoEstoqueGarantia ) AS D
ORDER BY numero;

UPDATE sqldados.produtoEstoqueGarantia AS G
  INNER JOIN T_NUMERO AS N
  ON G.numero = N.numero
SET G.numero = N.novo
WHERE G.numero != N.novo;

UPDATE sqldados.produtoObservacaoGarantia AS G
  INNER JOIN T_NUMERO AS N
  ON G.numero = N.numero
SET G.numero = N.novo
WHERE G.numero != N.novo;

select * from sqldados.produtoObservacaoGarantia



