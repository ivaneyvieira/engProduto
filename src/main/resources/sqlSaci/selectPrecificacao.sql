USE sqldados;

DO @CODIGO := :codigo;
DO @PRDNO := LPAD(@CODIGO, 16, ' ');
DO @LISTVEND := REPLACE(:listVend, ' ', '');
DO @TRIBUTACAO := :tributacao;
DO @MVA := CONCAT(:mva, '%');
DO @TYPENO := :typeno;
DO @CLNO := :clno;
DO @QUERY := :query;
DO @QUERYLIKE := CONCAT(@QUERY, '%');


DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT no AS prdno
FROM sqldados.prd
WHERE (mfno = 27142 OR :ultnota = 'N');


DROP TEMPORARY TABLE IF EXISTS T_ETIQUETAS;
CREATE TEMPORARY TABLE T_ETIQUETAS
(
  PRIMARY KEY (prdno)
)
SELECT prdno, GROUP_CONCAT(DISTINCT TRIM(text__256) ORDER BY seqno SEPARATOR '\\') AS impostos
FROM
  sqldados.prdetq2
    INNER JOIN T_PRD
               USING (prdno)
WHERE (text__256 LIKE 'ICMS ENTRADA%' OR text__256 LIKE 'MVA ORIGINAL%' OR text__256 LIKE 'TIMON - MA NCM%')
  AND prdno < LPAD('960001', 16, ' ')
GROUP BY prdno;

DROP TEMPORARY TABLE IF EXISTS T_NFD_ULT;
CREATE TEMPORARY TABLE T_NFD_ULT
(
  PRIMARY KEY (storeno, prdno)
)
SELECT N.storeno, I.prdno, MAX(invno) AS invno
FROM
  sqldados.inv               AS N
    INNER JOIN sqldados.iprd AS I
               USING (invno)
    INNER JOIN T_PRD
               USING (prdno)
WHERE N.bits & POW(2, 4) = 0
  AND N.invno NOT IN ( SELECT nfNfno FROM sqldados.inv WHERE auxShort13 & POW(2, 15) != 0 )
  AND N.type = 0
  AND N.storeno = IF(:loja = 10, 4, :loja)
  AND (I.prdno = @PRDNO OR @CODIGO = 0)
  AND (:ultnota = 'S')
GROUP BY N.storeno, I.prdno;

DROP TEMPORARY TABLE IF EXISTS T_NFD;
CREATE TEMPORARY TABLE T_NFD
(
  PRIMARY KEY (invno, storeno, prdno),
  nfIrst decimal(10, 2) NULL
)
SELECT :loja                                           AS storeno,
       I.prdno                                         AS prdno,
       U.invno                                         AS invno,
       SUM(I.fob / 100)                                AS nfValor,
       AVG(I.ipi / 100)                                AS nfIpi,
       NULL                                            AS nfIrst,
       AVG(I.icmsAliq / 100)                           AS nfIcms,
       SUM((I.dfob * I.qtty / 1000) * I.frete / 10000) AS freteCalc,
       SUM(I.frete / 100)                              AS frete
FROM
  sqldados.inv               AS N
    INNER JOIN sqldados.iprd AS I
               USING (invno)
    INNER JOIN T_NFD_ULT     AS U
               ON U.storeno = N.storeno AND U.prdno = I.prdno AND U.invno = N.invno
GROUP BY N.storeno, I.prdno, U.invno;

SELECT P.storeno                                                                                               AS loja,
       P.prdno                                                                                                 AS prdno,
       LPAD(TRIM(P.prdno), 6, '0')                                                                             AS codigo,
       TRIM(MID(PD.name, 1, 37))                                                                               AS descricao,
       PD.mfno                                                                                                 AS vendno,
       V.sname                                                                                                 AS fornecedor,
       ROUND(adm / 100, 2)                                                                                     AS cpmf,
       PD.taxno                                                                                                AS tributacao,
       PD.typeno                                                                                               AS typeno,
       PD.clno                                                                                                 AS clno,
       ROUND(IF(PD.taxno = '00', 0.00, IFNULL(PD.lucroTributado, 0)) / 100, 4)                                 AS mvap,
       P.dicm / 100                                                                                            AS icmsp,
       ROUND(pis / 100, 2)                                                                                     AS fcp,
       P.fob / 10000                                                                                           AS pcfabrica,
       P.ipi / 100                                                                                             AS ipi,
       P.package / 100                                                                                         AS embalagem,
       P.costdel3 / 100                                                                                        AS retido,
       IF(PD.taxno = '06', PD.auxShort1 / 100, 0.00)                                                           AS creditoICMS,
       P.freight / 100                                                                                         AS frete,
       P.auxLong4 / 100                                                                                        AS pisCofins,
       @C_CONTABIL := ROUND(P.fob / 10000, 4) + ROUND((P.fob / 10000) * (P.ipi / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * (P.package / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * (P.costdel3 / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * ((P.dicm - P.freight_icms) / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * (P.freight / 100) / 100, 4) + ROUND(
                          (ROUND(P.fob / 10000, 4) + ROUND((P.fob / 10000) * (P.ipi / 100) / 100, 4) +
                           ROUND((P.fob / 10000) * (P.package / 100) / 100, 4) +
                           ROUND((P.fob / 10000) * (P.freight / 100) / 100, 4)) * (P.auxLong4 / 100) / 100,
                          4)                                                                                   AS custoContabil,
       P.icm / 100                                                                                             AS icms,
       P.finsoc / 100                                                                                          AS pis,
       P.comm / 100                                                                                            AS ir,
       P.adv / 100                                                                                             AS contrib,
       P.refpdel2 / 100                                                                                        AS fixa,
       P.refpdel3 / 100                                                                                        AS outras,
       P.profit / 100                                                                                          AS lucroLiq,
       @PSUG := TRUNCATE((@C_CONTABIL) / ((100 -
                                           (((P.icm + P.pis + P.finsoc + comm + adv + adm + refpdel1 + refpdel2 + refpdel3) +
                                             profit) / 100)) / 100),
                         2)                                                                                    AS precoSug,
       @PREF := P.refprice / 100                                                                               AS precoRef,
       @PREF - @PSUG                                                                                           AS precoDif,
       S.ncm                                                                                                   AS ncm,
       R.form_label                                                                                            AS rotulo,
       P.freight_icms / 100                                                                                    AS freteICMS,
       TRUNCATE(P.cost / 10000, 2)                                                                             AS precoCusto,
       TRUNCATE(P.auxLong3 / 100, 2)                                                                           AS cfinanceiro,
       CAST(IFNULL(E.impostos, '') AS CHAR)                                                                    AS impostos,
       nfValor                                                                                                 AS nfValor,
       nfIpi                                                                                                   AS nfIpi,
       nfIrst                                                                                                  AS nfIrst,
       nfIcms                                                                                                  AS nfIcms,
       freteCalc                                                                                               AS nfFrete
FROM
  sqldados.prp                  AS P
    INNER JOIN T_PRD
               USING (prdno)
    INNER JOIN sqldados.prd     AS PD
               ON PD.no = P.prdno
    INNER JOIN sqldados.spedprd AS S
               USING (prdno)
    LEFT JOIN  sqldados.prdalq  AS R
               USING (prdno)
    INNER JOIN sqldados.vend    AS V
               ON PD.mfno = V.no
    LEFT JOIN  T_ETIQUETAS      AS E
               USING (prdno)
    LEFT JOIN  T_NFD            AS N
               ON N.storeno = P.storeno AND N.prdno = P.prdno
WHERE P.storeno = :loja
  AND P.prdno < LPAD('960001', 16, ' ')
  AND (P.prdno = @PRDNO OR @CODIGO = 0)
  AND (ROUND(IF(PD.taxno = '00', 0.00, IFNULL(PD.lucroTributado, 0)) / 100, 4) LIKE @MVA OR @MVA = '')
  AND (FIND_IN_SET(PD.mfno, @LISTVEND) OR @LISTVEND = '')
  AND (FIND_IN_SET(PD.typeno, @TYPENO) OR @TYPENO = '')
  AND (PD.clno = @CLNO OR PD.deptno = @CLNO OR PD.groupno = @CLNO OR @CLNO = 0)
  AND (PD.taxno = @TRIBUTACAO OR @TRIBUTACAO = '')
  AND CASE :marca
        WHEN 'T' THEN TRUE
        WHEN 'N' THEN MID(PD.name, 1, 1) NOT IN ('.', '*', '!', '*', ']', ':', '#')
        WHEN 'S' THEN MID(PD.name, 1, 1) IN ('.', '*', '!', '*', ']', ':', '#')
      END
HAVING @QUERY = ''
    OR descricao LIKE @QUERYLIKE
    OR ncm LIKE @QUERYLIKE
    OR rotulo LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(mvap, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(icmsp, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(fcp, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(pcfabrica, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(ipi, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(embalagem, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(retido, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(creditoICMS, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(frete, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(custoContabil, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(icms, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(pis, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(ir, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(contrib, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(fixa, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(outras, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(lucroLiq, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(precoSug, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(precoRef, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(freteICMS, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
    OR REPLACE(REPLACE(FORMAT(cfinanceiro, 2), ',', ''), '.', ',') LIKE @QUERYLIKE
