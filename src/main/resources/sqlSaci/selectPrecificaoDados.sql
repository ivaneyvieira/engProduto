USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, storeno)
)
SELECT P.no AS prdno, S.no AS storeno, P.name AS descrivao, taxno
FROM sqldados.prd AS P, sqldados.store AS S
WHERE S.no IN (2, 3, 4, 5, 8, 10)/*
  AND P.no = '              19'*/;

/*
 select * from prp where prdno = '              19' and storeno = 10
 */

DROP TEMPORARY TABLE IF EXISTS T_PRP;
CREATE TEMPORARY TABLE T_PRP
(
  PRIMARY KEY (prdno, storeno)
)
SELECT storeno,
       prdno,
       descrivao,
       taxno,
       fob / 10000                      AS precoFabrica,
       ipi / 100                        AS percentualIPI,
       dicm / 100                       AS creditoICMS,
       package / 100                    AS embalagem,
       ROUND(P.fob / 10000, 4) + ROUND((P.fob / 10000) * (P.ipi / 100) / 100, 4) +
       ROUND((P.fob / 10000) * (P.package / 100) / 100, 4) + ROUND((P.fob / 10000) * (P.costdel3 / 100) / 100, 4) +
       ROUND((P.fob / 10000) * (P.dicm / 100) / 100, 4) + ROUND((P.fob / 10000) * (P.freight / 100) / 100, 4) + ROUND(
           (ROUND(P.fob / 10000, 4) + ROUND((P.fob / 10000) * (P.ipi / 100) / 100, 4) +
            ROUND((P.fob / 10000) * (P.package / 100) / 100, 4) + ROUND((P.fob / 10000) * (P.freight / 100) / 100, 4)) *
           (P.auxLong4 / 100) / 100, 4) AS custoContabil,
       P.auxLong4 / 100                 AS creditoPisCofins,
       P.freight / 100                  AS frete
FROM
  T_PRD
    LEFT JOIN sqldados.prp AS P
              USING (prdno, storeno)
WHERE (:pesquisa = '' OR descrivao LIKE CONCAT(:pesquisa, '%') OR taxno LIKE :pesquisa OR TRIM(prdno) LIKE :pesquisa);

SELECT prdno                                         AS prdno,
       descrivao                                     AS descricao,
       taxno                                         AS taxno,
       SUM(IF(storeno = 2, precoFabrica, NULL))      AS precoFabrica02,
       SUM(IF(storeno = 3, precoFabrica, NULL))      AS precoFabrica03,
       SUM(IF(storeno = 4, precoFabrica, NULL))      AS precoFabrica04,
       SUM(IF(storeno = 5, precoFabrica, NULL))      AS precoFabrica05,
       SUM(IF(storeno = 8, precoFabrica, NULL))      AS precoFabrica08,
       SUM(IF(storeno = 10, precoFabrica, NULL))     AS precoFabrica10,
       SUM(IF(storeno = 2, percentualIPI, NULL))     AS percentualIPI02,
       SUM(IF(storeno = 3, percentualIPI, NULL))     AS percentualIPI03,
       SUM(IF(storeno = 4, percentualIPI, NULL))     AS percentualIPI04,
       SUM(IF(storeno = 5, percentualIPI, NULL))     AS percentualIPI05,
       SUM(IF(storeno = 8, percentualIPI, NULL))     AS percentualIPI08,
       SUM(IF(storeno = 10, percentualIPI, NULL))    AS percentualIPI10,
       SUM(IF(storeno = 2, creditoICMS, NULL))       AS creditoICMS02,
       SUM(IF(storeno = 3, creditoICMS, NULL))       AS creditoICMS03,
       SUM(IF(storeno = 4, creditoICMS, NULL))       AS creditoICMS04,
       SUM(IF(storeno = 5, creditoICMS, NULL))       AS creditoICMS05,
       SUM(IF(storeno = 8, creditoICMS, NULL))       AS creditoICMS08,
       SUM(IF(storeno = 10, creditoICMS, NULL))      AS creditoICMS10,
       SUM(IF(storeno = 2, embalagem, NULL))         AS embalagem02,
       SUM(IF(storeno = 3, embalagem, NULL))         AS embalagem03,
       SUM(IF(storeno = 4, embalagem, NULL))         AS embalagem04,
       SUM(IF(storeno = 5, embalagem, NULL))         AS embalagem05,
       SUM(IF(storeno = 8, embalagem, NULL))         AS embalagem08,
       SUM(IF(storeno = 10, embalagem, NULL))        AS embalagem10,
       SUM(IF(storeno = 2, custoContabil, NULL))     AS custoContabil02,
       SUM(IF(storeno = 3, custoContabil, NULL))     AS custoContabil03,
       SUM(IF(storeno = 4, custoContabil, NULL))     AS custoContabil04,
       SUM(IF(storeno = 5, custoContabil, NULL))     AS custoContabil05,
       SUM(IF(storeno = 8, custoContabil, NULL))     AS custoContabil08,
       SUM(IF(storeno = 10, custoContabil, NULL))    AS custoContabil10,
       SUM(IF(storeno = 2, creditoPisCofins, NULL))  AS creditoPisCofins02,
       SUM(IF(storeno = 3, creditoPisCofins, NULL))  AS creditoPisCofins03,
       SUM(IF(storeno = 4, creditoPisCofins, NULL))  AS creditoPisCofins04,
       SUM(IF(storeno = 5, creditoPisCofins, NULL))  AS creditoPisCofins05,
       SUM(IF(storeno = 7, creditoPisCofins, NULL))  AS creditoPisCofins08,
       SUM(IF(storeno = 10, creditoPisCofins, NULL)) AS creditoPisCofins10,
       SUM(IF(storeno = 2, frete, NULL))             AS frete02,
       SUM(IF(storeno = 3, frete, NULL))             AS frete03,
       SUM(IF(storeno = 4, frete, NULL))             AS frete04,
       SUM(IF(storeno = 5, frete, NULL))             AS frete05,
       SUM(IF(storeno = 8, frete, NULL))             AS frete08,
       SUM(IF(storeno = 10, frete, NULL))            AS frete10
FROM T_PRP
GROUP BY prdno
ORDER BY prdno;