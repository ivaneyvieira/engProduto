USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_NI;
CREATE TEMPORARY TABLE T_NI
SELECT invno
FROM
  sqldados.invAdicional
WHERE situacaoDev = :situacaoDev
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

DROP TEMPORARY TABLE IF EXISTS T_NI2;
CREATE TEMPORARY TABLE T_NI2
SELECT invno
FROM
  sqldados.iprdAdicionalDev         AS D
    LEFT JOIN sqldados.invAdicional AS A
              USING (invno, tipoDevolucao, numero)
WHERE A.invno IS NULL
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero
GROUP BY invno;

REPLACE sqldados.invAdicional(invno, numero, tipoDevolucao, volume, peso, carrno, cet, dataDevolucao, situacaoDev,
                              userno, observacao, dataColeta, observacaoAdicional)
SELECT invno,
       :numero,
       :tipoDevolucao,
       :volume,
       :peso,
       :transp,
       :cte,
       :data,
       :situacaoDevNovo,
       :userno,
       :observacaoDev,
       :dataColeta,
       :observacaoAdicional
FROM
  sqldados.invAdicional
WHERE invno IN ( SELECT invno FROM T_NI )
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero

UNION

SELECT invno,
       :numero,
       :tipoDevolucao,
       :volume,
       :peso,
       :transp,
       :cte,
       :data,
       :situacaoDevNovo,
       :userno,
       :observacaoDev,
       :dataColeta,
       :observacaoAdicional
FROM
  T_NI2
WHERE NOT EXISTS ( SELECT *
                   FROM
                     sqldados.invAdicional
                   WHERE situacaoDev = :situacaoDev
                     AND tipoDevolucao = :tipoDevolucao
                     AND numero = :numero )
