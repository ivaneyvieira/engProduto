USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno AS prdno, A.grade AS grade, TRIM(MID(A.localizacao, 1, 4)) AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE (A.storeno = 4);

DROP TEMPORARY TABLE IF EXISTS T_BARCODE;
CREATE TEMPORARY TABLE T_BARCODE
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, GROUP_CONCAT(DISTINCT TRIM(barcode)) AS barcodeList
FROM
  sqldados.prdbar
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  PRIMARY KEY (codigo, grade)
)
SELECT X.storeno                                        AS loja,
       pdvno                                            AS pdvno,
       xano                                             AS xano,
       CAST(TRIM(P.no) AS CHAR)                         AS codigo,
       X.prdno                                          AS prdno,
       IFNULL(X.grade, '')                              AS grade,
       TRIM(MID(P.name, 1, 37))                         AS descricao,
       TRIM(MID(P.name, 37, 3))                         AS un,
       IF(X.grade = '',
          CONCAT(IFNULL(B.barcodeList, ''), IF(IFNULL(B.barcodeList, '') = '', '', ','), TRIM(P.barcode)),
          COALESCE(B.barcodeList, TRIM(P.barcode), '')) AS barcodeStrList,
       L.localizacao                                    AS local,
       X.cfo1                                           AS cfop,
       S.cstPis                                         AS cst,
       IFNULL(S.ncm, '')                                AS ncm,
       X.qtty / 1000                                    AS quantidade,
       X.preco                                          AS preco,
       X.desconto / 100                                 AS desconto,
       X.auxMy2 / 100                                   AS frete,
       X.outras / 100                                   AS despesas,
       X.baseCalculoIcms / 100                          AS baseIcms,
       X.baseCalculoSubst / 100                         AS baseSubst,
       X.amtSubst / 100                                 AS valorSubst,
       X.icmsAmt / 100                                  AS valorIcms,
       X.ipiAmt / 100                                   AS valorIpi,
       X.icmsAliq / 100                                 AS aliquotaIcms,
       X.ipiAliq / 100                                  AS aliquotaIpi,
       (X.qtty / 1000) * X.preco                        AS total
FROM
  sqldados.prd                  AS P
    INNER JOIN sqldados.xaprd2  AS X
               ON P.no = X.prdno
    LEFT JOIN  T_LOC            AS L
               USING (prdno, grade)
    LEFT JOIN  T_BARCODE        AS B
               USING (prdno, grade)
    LEFT JOIN  sqldados.spedprd AS S
               ON P.no = S.prdno
WHERE X.storeno = :storeno
  AND X.pdvno = :pdvno
  AND X.xano = :xano
GROUP BY prdno, grade;

SELECT loja,
       pdvno,
       xano,
       codigo,
       prdno,
       grade,
       descricao,
       un,
       cfop,
       cst,
       ncm,
       barcodeStrList,
       local,
       quantidade,
       preco,
       desconto,
       frete,
       despesas,
       baseIcms,
       baseSubst,
       valorSubst,
       valorIcms,
       valorIpi,
       aliquotaIcms,
       aliquotaIpi,
       total
FROM
  T_DADOS

