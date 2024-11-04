USE sqldados;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT(@PESQUISA, '%');

DROP TABLE IF EXISTS T_PRD_FILTER;
CREATE TEMPORARY TABLE T_PRD_FILTER
(
  PRIMARY KEY (prdno)
)
SELECT P.no AS prdno
FROM sqldados.prd AS P
WHERE (:vendno = 0 OR P.mfno = :vendno)
  AND (:taxno = '' OR P.taxno = :taxno)
  AND (:typeno = 0 OR P.typeno = :typeno)
  AND (:clno = 0 OR P.clno = :clno OR P.groupno = :clno OR P.deptno = :clno)
  AND (
  :caracter = 'T' OR
  (:caracter = 'S' AND P.name NOT REGEXP '^[A-Z0-9]') OR
  (:caracter = 'N' AND P.name REGEXP '^[A-Z0-9]')
  )
  AND (
  :letraDup = 'T' OR
  (:letraDup = 'S' AND SUBSTRING_INDEX(P.name, ' ', 1) REGEXP
                       'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ') OR
  (:letraDup = 'N' AND SUBSTRING_INDEX(P.name, ' ', 1) NOT REGEXP
                       'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ')
  )
  AND (:consumo = 'T' OR
       (:consumo = 'S' AND P.no * 1 >= 900000) OR
       (:consumo = 'N' AND P.no * 1 < 900000)
  )
GROUP BY P.no;

DROP TEMPORARY TABLE IF EXISTS T_PRD_ST;
CREATE TEMPORARY TABLE T_PRD_ST
(
  PRIMARY KEY (prdno)
)
SELECT prdno,
       COUNT(DISTINCT storeno)                         AS ctLoja,
       GROUP_CONCAT(DISTINCT storeno ORDER BY storeno) AS lojas,
       SUM((POW(2, 0) & bits) != 0)                    AS ctIpi,
       SUM((POW(2, 1) & bits) != 0)                    AS ctPis,
       SUM((POW(2, 4) & bits) != 0)                    AS ctIcms,
       SUM(((POW(2, 1) & bits) = 0) OR (aliqPis != 165) OR
           (aliqCofins != 760) OR (cstPis != 1) OR
           (cstCofins != 1) OR (cstPisIn != 50) OR
           (cstCofinsIn != 50))                        AS ctErroPisCofins,
       SUM(CASE auxStr1
             WHEN 'NORMAL..' THEN IF(storeno = 8, auxStr2 NOT IN ('01', '25'), auxStr2 NOT IN ('00', '25'))
             WHEN 'ISENTO..' THEN auxStr2 != '04'
             WHEN 'NAO_TRIB' THEN auxStr2 != '41'
             WHEN 'SUBSTIFC' THEN auxStr2 != '06'
             WHEN 'SUBSTI00' THEN auxStr2 != '06'
             WHEN 'REDUZI56' THEN IF(storeno = 8, auxStr2 != '21', auxStr2 != '20')
             WHEN 'REDUZI88' THEN IF(storeno = 8, auxStr2 != '21', auxStr2 != '20')
             ELSE 1
           END)                                        AS ctErroRotulo
FROM sqldados.spedprdst
       INNER JOIN T_PRD_FILTER
                  USING (prdno)
GROUP BY prdno;

/*
select storeno, prdno, auxStr1, auxStr2
from  sqldados.spedprdst
where prdno = 123446
*/

DROP TEMPORARY TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (prdno)
)
SELECT F.prdno                                                             AS prdno,
       ROUND(SUM(IFNULL(S.qtty_varejo, 0) / 1000))                         AS qttyVarejo,
       ROUND(SUM(IFNULL(S.qtty_atacado, 0) / 1000))                        AS qttyAtacado,
       ROUND(SUM(IFNULL(S.qtty_varejo, 0) / 1000 + S.qtty_atacado / 1000)) AS qttyTotal
FROM T_PRD_FILTER AS F
       LEFT JOIN sqldados.stk AS S
                 ON F.prdno = S.prdno
                   AND (S.storeno IN (1, 2, 3, 4, 5, 8))
GROUP BY F.prdno;

DROP TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  INDEX (ctLoja)
)
SELECT PD.no                                    AS prdno,
       TRIM(PD.no) * 1                          AS codigo,
       TRIM(MID(PD.name, 1, 37))                AS descricao,
       TRIM(MID(PD.name, 37, 3))                AS unidade,
       IFNULL(R.form_label, '')                 AS rotulo,
       PD.taxno                                 AS tributacao,
       V.no                                     AS forn,
       V.sname                                  AS abrev,
       IFNULL(S.ncm, '')                        AS ncm,
       PD.typeno                                AS tipo,
       PD.clno                                  AS clno,
       PD.mfno_ref                              AS refForn,
       PD.weight_g                              AS pesoBruto,
       CASE PD.tipoGarantia
         WHEN 0 THEN 'Dia'
         WHEN 1 THEN 'Semana'
         WHEN 2 THEN 'Mês'
         WHEN 3 THEN 'Ano'
         ELSE ''
       END                                      AS uGar,
       PD.garantia                              AS tGar,
       PD.qttyPackClosed / 1000                 AS emb,
       IF((PD.dereg & POW(2, 2)) = 0, 'N', 'S') AS foraLinha,
       SUM(STK.qttyTotal)                       AS saldo,
       IFNULL(ST.ctLoja, 0)                     AS ctLoja,
       IFNULL(ctIpi, 0)                         AS ctIpi,
       IFNULL(ctPis, 0)                         AS ctPis,
       IFNULL(ctIcms, 0)                        AS ctIcms,
       IFNULL(ctErroPisCofins, 0)               AS ctErroPisCofins,
       IFNULL(ctErroRotulo, 0)                  AS ctErroRotulo,
       IFNULL(ST.lojas, '')                     AS lojas
FROM T_PRD_FILTER AS PF
       LEFT JOIN sqldados.prd AS PD
                 ON PF.prdno = PD.no
       LEFT JOIN sqldados.prdalq AS R
                 ON R.prdno = PD.no
       LEFT JOIN sqldados.vend AS V
                 ON V.no = PD.mfno
       LEFT JOIN sqldados.spedprd AS S
                 ON PD.no = S.prdno
       LEFT JOIN T_STK AS STK
                 ON STK.prdno = PF.prdno
       LEFT JOIN T_PRD_ST AS ST
                 ON ST.prdno = PF.prdno
WHERE (R.form_label LIKE CONCAT(:rotulo, '%') OR :rotulo = '')
GROUP BY PF.prdno;

SELECT prdno,
       codigo,
       descricao,
       unidade,
       rotulo,
       tributacao,
       forn,
       abrev,
       ncm,
       tipo,
       clno,
       refForn,
       pesoBruto,
       uGar,
       tGar,
       emb,
       foraLinha,
       saldo,
       ctLoja,
       ctIpi,
       ctPis,
       ctIcms,
       ctErroPisCofins,
       ctErroRotulo,
       lojas
FROM T_PRD
WHERE (:pesquisa = ''
  OR codigo LIKE @PESQUISA
  OR descricao LIKE @PESQUISA_LIKE
  OR unidade LIKE @PESQUISA_LIKE
  OR abrev LIKE @PESQUISA_LIKE
  OR ncm LIKE @PESQUISA)
  AND ((:configSt = 'N')
  OR (:configSt = 'S' AND ctLoja = 0))
  AND ((:pisCofN = 'N')
  OR (:pisCofN = 'S' AND ctErroPisCofins > 0))
  AND ((:rotuloN = 'N')
  OR (:rotuloN = 'S' AND ctErroRotulo > 0))
