USE sqldados;

SELECT MAX(quant) AS quant
FROM
  ( SELECT MAX(numero + 1) AS quant
    FROM
      produtoEstoqueAcerto
    WHERE numloja = :numLoja
    UNION
    SELECT MAX(numero + 1) AS quant
    FROM
      produtoEstoqueGarantia
    WHERE numloja = :numLoja ) AS D