USE sqldados;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT(@PESQUISA, '%');


DROP TABLE IF EXISTS T_PRD_FILTER;
CREATE TEMPORARY TABLE T_PRD_FILTER
(
  PRIMARY KEY (prdno)
)
SELECT P.no AS prdno
FROM
  sqldados.prd AS P
WHERE (:vendno = 0 OR P.mfno = :vendno)
  AND (:taxno = '' OR P.taxno = :taxno)
  AND (:typeno = 0 OR P.typeno = :typeno)
  AND (:clno = 0 OR P.clno = :clno OR P.groupno = :clno OR P.deptno = :clno)
  AND (:caracter = 'T' OR (:caracter = 'S' AND P.name NOT REGEXP '^[A-Z0-9]') OR
       (:caracter = 'N' AND P.name REGEXP '^[A-Z0-9]'))
  AND (:letraDup = 'T' OR (:letraDup = 'S' AND SUBSTRING_INDEX(P.name, ' ', 1) REGEXP
                                               'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ') OR
       (:letraDup = 'N' AND SUBSTRING_INDEX(P.name, ' ', 1) NOT REGEXP
                            'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ'))
GROUP BY P.no;

DROP TEMPORARY TABLE IF EXISTS T_PRD_ST;
CREATE TEMPORARY TABLE T_PRD_ST
(
  PRIMARY KEY (prdno)
)
SELECT prdno, COUNT(DISTINCT storeno) AS ctLoja, GROUP_CONCAT(DISTINCT storeno ORDER BY storeno) AS lojas
FROM
  sqldados.spedprdst
    INNER JOIN T_PRD_FILTER
               USING (prdno)
GROUP BY prdno;

DROP TEMPORARY TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (prdno)
)
SELECT F.prdno                                                             AS prdno,
       ROUND(SUM(IFNULL(S.qtty_varejo, 0) / 1000))                         AS qttyVarejo,
       ROUND(SUM(IFNULL(S.qtty_atacado, 0) / 1000))                        AS qttyAtacado,
       ROUND(SUM(IFNULL(S.qtty_varejo, 0) / 1000 + S.qtty_atacado / 1000)) AS qttyTotal
FROM
  T_PRD_FILTER             AS F
    LEFT JOIN sqldados.stk AS S
              ON F.prdno = S.prdno AND (S.storeno IN (1, 2, 3, 4, 5, 8))
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
       TRIM(PD.free_fld1)                       AS convEnt,
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
       IFNULL(ST.lojas, '')                     AS lojas
FROM
  T_PRD_FILTER                 AS PF
    LEFT JOIN sqldados.prd     AS PD
              ON PF.prdno = PD.no
    LEFT JOIN sqldados.prdalq  AS R
              ON R.prdno = PD.no
    LEFT JOIN sqldados.vend    AS V
              ON V.no = PD.mfno
    LEFT JOIN sqldados.spedprd AS S
              ON PD.no = S.prdno
    LEFT JOIN T_STK            AS STK
              ON STK.prdno = PF.prdno
    LEFT JOIN T_PRD_ST         AS ST
              ON ST.prdno = PF.prdno
WHERE (R.form_label LIKE CONCAT(:rotulo, '%') OR :rotulo = '')
GROUP BY PF.prdno;

SELECT prdno,
       codigo,
       descricao,
       unidade,
       rotulo,
       convEnt,
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
       lojas
FROM
  T_PRD
WHERE (:pesquisa = '' OR codigo LIKE @PESQUISA OR descricao LIKE @PESQUISA_LIKE OR unidade LIKE @PESQUISA_LIKE OR
       abrev LIKE @PESQUISA_LIKE OR ncm LIKE @PESQUISA)
