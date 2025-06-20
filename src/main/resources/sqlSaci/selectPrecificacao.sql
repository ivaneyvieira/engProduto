DO @CODIGO := :codigo;
DO @PRDNO := LPAD(@CODIGO, 16, ' ');
DO @LISTVEND := REPLACE(:listVend, ' ', '');
DO @TRIBUTACAO := :tributacao;
DO @MVA := CONCAT(:mva, '%');
DO @TYPENO := :typeno;
DO @CLNO := :clno;
DO @QUERY := :query;
DO @QUERYLIKE := CONCAT(@QUERY, '%');

DROP TEMPORARY TABLE IF EXISTS T_ETIQUETAS;
CREATE TEMPORARY TABLE T_ETIQUETAS
(
  PRIMARY KEY (prdno)
)
SELECT prdno, GROUP_CONCAT(DISTINCT TRIM(text__256) ORDER BY seqno SEPARATOR '\\') AS impostos
FROM
  sqldados.prdetq2
WHERE (text__256 LIKE 'ICMS ENTRADA%' OR text__256 LIKE 'MVA ORIGINAL%' OR text__256 LIKE 'TIMON - MA NCM%')
  AND prdno < LPAD('960001', 16, ' ')
GROUP BY prdno;

SELECT prdno                                                                   AS prdno,
       LPAD(TRIM(prdno), 6, '0')                                               AS codigo,
       TRIM(MID(PD.name, 1, 37))                                               AS descricao,
       PD.mfno                                                                 AS vendno,
       V.sname                                                                 AS fornecedor,
       ROUND(adm / 100, 2)                                                     AS cpmf,
       PD.taxno                                                                AS tributacao,
       PD.typeno                                                               AS typeno,
       PD.clno                                                                 AS clno,
       ROUND(IF(PD.taxno = '00', 0.00, IFNULL(PD.lucroTributado, 0)) / 100, 4) AS mvap,
       P.dicm / 100                                                            AS icmsp,
       ROUND(pis / 100, 2)                                                     AS fcp,
       P.fob / 10000                                                           AS pcfabrica,
       P.ipi / 100                                                             AS ipi,
       P.package / 100                                                         AS embalagem,
       P.costdel3 / 100                                                        AS retido,
       IF(PD.taxno = '06', PD.auxShort1 / 100, 0.00)                           AS creditoICMS,
       P.freight / 100                                                         AS frete,
       @C_CONTABIL := ROUND(P.fob / 10000, 4) + ROUND((P.fob / 10000) * (P.ipi / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * (P.package / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * (P.costdel3 / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * ((P.dicm - P.freight_icms) / 100) / 100, 4) +
                      ROUND((P.fob / 10000) * (P.freight / 100) / 100, 4) + ROUND(
                          (ROUND(P.fob / 10000, 4) + ROUND((P.fob / 10000) * (P.ipi / 100) / 100, 4) +
                           ROUND((P.fob / 10000) * (P.package / 100) / 100, 4) +
                           ROUND((P.fob / 10000) * (P.freight / 100) / 100, 4)) * (P.auxLong4 / 100) / 100,
                          4)                                                   AS custoContabil,
       P.icm / 100                                                             AS icms,
       P.finsoc / 100                                                          AS pis,
       P.comm / 100                                                            AS ir,
       P.adv / 100                                                             AS contrib,
       P.refpdel2 / 100                                                        AS fixa,
       P.refpdel3 / 100                                                        AS outras,
       P.profit / 100                                                          AS lucroLiq,
       @PSUG := TRUNCATE((@C_CONTABIL) / ((100 -
                                           (((P.icm + P.pis + P.finsoc + comm + adv + adm + refpdel1 + refpdel2 + refpdel3) +
                                             profit) / 100)) / 100),
                         2)                                                    AS precoSug,
       @PREF := P.refprice / 100                                               AS precoRef,
       @PREF - @PSUG                                                           AS precoDif,
       S.ncm                                                                   AS ncm,
       R.form_label                                                            AS rotulo,
       P.freight_icms / 100                                                    AS freteICMS,
       TRUNCATE(P.cost / 10000, 2)                                             AS precoCusto,
       TRUNCATE(P.auxLong3 / 100, 2)                                           AS cfinanceiro,
       CAST(IFNULL(E.impostos, '') AS CHAR)                                    AS impostos
FROM
  sqldados.prp                  AS P
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
WHERE P.storeno = 10
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
