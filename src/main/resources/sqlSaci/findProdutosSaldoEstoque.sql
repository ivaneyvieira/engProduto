USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT A.prdno AS prdno, IF(:grade, A.grade, '') AS gradeProduto, COALESCE(A.localizacao, '') AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE A.storeno = 4
  AND A.localizacao != ''
GROUP BY A.prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT P.no                                    AS prdno,
       TRIM(P.no) * 1                          AS codigo,
       TRIM(MID(P.name, 1, 37))                AS descricao,
       P.unit                                  AS unidade,
       P.taxno                                 AS tributacao,
       IFNULL(R.form_label, '')                AS rotulo,
       N.ncm                                   AS ncm,
       P.mfno                                  AS fornecedor,
       V.sname                                 AS abrev,
       P.typeno                                AS tipo,
       P.clno                                  AS cl,
       IF((P.bits % POW(2, 10)) > 0, 'S', 'N') AS temRelacionado
FROM
  sqldados.prd                 AS P
    LEFT JOIN sqldados.prdalq  AS R
              ON R.prdno = P.no
    LEFT JOIN sqldados.spedprd AS N
              ON N.prdno = P.no
    LEFT JOIN sqldados.vend    AS V
              ON V.no = P.mfno
WHERE (P.mfno = :fornecedor OR :fornecedor = 0)
  AND (P.taxno = :tributacao OR :tributacao = '')
  AND (R.form_label = :rotulo OR :rotulo = '')
  AND (P.typeno = :tipo OR :tipo = 0)
  AND (P.clno = :cl OR P.deptno = :cl OR P.groupno = :cl OR :cl = 0)
  AND CASE :caracter
        WHEN 'S' THEN P.name NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN P.name REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
                 ELSE FALSE
      END
  AND CASE :letraDup
        WHEN 'S' THEN SUBSTRING_INDEX(P.name, ' ', 1) REGEXP
                      'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ'
        WHEN 'N' THEN SUBSTRING_INDEX(P.name, ' ', 1) NOT REGEXP
                      'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ'
        WHEN 'T' THEN TRUE
                 ELSE FALSE
      END
  AND (
  (:consumo = 'T') OR
  (:consumo = 'S' AND ((P.no * 1 >= 900000) OR (P.bits & POW(2, 13) > 0))) OR
  (:consumo = 'N' AND NOT ((P.no * 1 >= 900000) OR (P.bits & POW(2, 13) > 0)))
  );

DROP TEMPORARY TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (loja, prdno, gradeProduto),
  INDEX (qttyTotal)
)
SELECT S.storeno                                                       AS loja,
       S.prdno                                                         AS prdno,
       IF(:grade, S.grade, '')                                         AS gradeProduto,
       (SUM(S.qtty2 / 1000))                                           AS qttyVarejo,
       (SUM((S.qtty - S.qtty2) / 1000))                                AS qttyAtacado,
       (SUM(S.qtty / 1000))                                            AS qttyTotal,
       SUM((S.cost2 / 10000) * (S.qtty2 / 1000)) / SUM(S.qtty2 / 1000) AS custoVarejo
FROM
  sqldados.stkchk AS S
    INNER JOIN T_PRD
               USING (prdno)
WHERE (S.storeno = :loja)
  AND (ym = :ym)
GROUP BY ym, loja, prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_PRDSTK;
CREATE TEMPORARY TABLE T_PRDSTK
(
  PRIMARY KEY (loja, prdno, gradeProduto)
)
SELECT S.loja                                                AS loja,
       S.prdno                                               AS prdno,
       P.codigo * 1                                          AS codigo,
       P.descricao                                           AS descricao,
       S.gradeProduto                                        AS gradeProduto,
       P.unidade                                             AS unidade,
       S.qttyVarejo                                          AS qttyVarejo,
       S.qttyAtacado                                         AS qttyAtacado,
       S.qttyTotal                                           AS qttyTotal,
       S.custoVarejo                                         AS custoVarejo,
       IFNULL(S.custoVarejo, 0.00) * IFNULL(S.qttyVarejo, 0) AS custoTotal,
       P.tributacao                                          AS tributacao,
       P.rotulo                                              AS rotulo,
       P.ncm                                                 AS ncm,
       P.fornecedor                                          AS fornecedor,
       P.abrev                                               AS abrev,
       P.tipo                                                AS tipo,
       P.cl                                                  AS cl,
       MID(L.localizacao, 1, 4)                              AS localizacao
FROM
  T_STK              AS S
    LEFT JOIN  T_LOC AS L
               USING (prdno, gradeProduto)
    INNER JOIN T_PRD AS P
               USING (prdno)
WHERE (S.loja = :loja OR :loja = 0)
  AND (((:tipoSaldo = 'TOTAL') AND
        ((:estoque = '<' AND S.qttyTotal < :saldo) OR (:estoque = '>' AND S.qttyTotal > :saldo) OR
         (:estoque = '=' AND S.qttyTotal = :saldo) OR (:estoque = 'T'))) OR ((:tipoSaldo = 'VAREJO') AND
                                                                             ((:estoque = '<' AND S.qttyVarejo < :saldo) OR
                                                                              (:estoque = '>' AND S.qttyVarejo > :saldo) OR
                                                                              (:estoque = '=' AND S.qttyVarejo = :saldo) OR
                                                                              (:estoque = 'T'))) OR
       ((:tipoSaldo = 'ATACADO') AND
        ((:estoque = '<' AND S.qttyAtacado < :saldo) OR (:estoque = '>' AND S.qttyAtacado > :saldo) OR
         (:estoque = '=' AND S.qttyAtacado = :saldo) OR (:estoque = 'T'))));

SELECT loja,
       prdno,
       codigo,
       descricao,
       gradeProduto,
       unidade,
       qttyVarejo,
       qttyAtacado,
       qttyTotal,
       custoTotal,
       custoVarejo,
       tributacao,
       rotulo,
       ncm,
       fornecedor,
       abrev,
       tipo,
       cl,
       localizacao
FROM
  T_PRDSTK AS S
WHERE (@PESQUISA = '' OR codigo = @PESQUISA OR descricao LIKE @PESQUISA_LIKE OR gradeProduto LIKE @PESQUISA_START OR
       unidade = @PESQUISA OR tributacao = @PESQUISA OR rotulo LIKE @PESQUISA_START OR ncm LIKE @PESQUISA_START OR
       fornecedor = @PESQUISA OR abrev LIKE @PESQUISA_LIKE OR cl = @PESQUISA)