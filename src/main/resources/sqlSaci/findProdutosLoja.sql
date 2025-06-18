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
SELECT A.prdno AS prdno, IF(:grade = 'S', A.grade, '') AS gradeProduto, COALESCE(A.localizacao, '') AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE A.storeno = 4
  AND A.localizacao != ''
GROUP BY A.prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_REL;
CREATE TEMPORARY TABLE T_REL
(
  PRIMARY KEY (prdno, temRelacionado)
)
SELECT prdno AS prdnoRel, prdno_rel AS prdno, 'S' AS temRelacionado
FROM
  sqldados.prdrel
GROUP BY prdno_rel;


DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT P.no                                    AS prdno,
       TRIM(P.no) * 1                          AS codigo,
       TRIM(MID(P.name, 1, 37))                AS descricao,
       TRIM(MID(P.name, 38, 3))                AS unidade,
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
  AND (P.taxno = :tributacao OR :tributacao = 0)
  AND (R.form_label = :rotulo OR :rotulo = '')
  AND (N.ncm = :ncm OR :ncm = '')
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
  AND (:consumo = 'T' OR (:consumo = 'S' AND P.no * 1 >= 900000) OR (:consumo = 'N' AND P.no * 1 < 900000));

DROP TEMPORARY TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (prdno, gradeProduto),
  INDEX (estoqueTotal)
)
SELECT S.prdno                                                                        AS prdno,
       IF(:grade = 'S', S.grade, '')                                                  AS gradeProduto,
       ROUND(SUM(S.qtty_varejo / 1000 + S.qtty_atacado / 1000))                       AS estoqueTotal,
       ROUND(SUM(IF(S.storeno = 2, S.qtty_varejo / 1000 + S.qtty_atacado / 1000, 0))) AS estoqueDS,
       ROUND(SUM(IF(S.storeno = 3, S.qtty_varejo / 1000 + S.qtty_atacado / 1000, 0))) AS estoqueMR,
       ROUND(SUM(IF(S.storeno = 4, S.qtty_varejo / 1000 + S.qtty_atacado / 1000, 0))) AS estoqueMF,
       ROUND(SUM(IF(S.storeno = 5, S.qtty_varejo / 1000 + S.qtty_atacado / 1000, 0))) AS estoquePK,
       ROUND(SUM(IF(S.storeno = 8, S.qtty_varejo / 1000 + S.qtty_atacado / 1000, 0))) AS estoqueTM
FROM
  sqldados.stk AS S
WHERE (S.storeno IN (2, 3, 4, 5, 8))
GROUP BY prdno, gradeProduto;


DROP TEMPORARY TABLE IF EXISTS T_PRDSTK;
CREATE TEMPORARY TABLE T_PRDSTK
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT S.prdno                  AS prdno,
       P.codigo * 1             AS codigo,
       P.descricao              AS descricao,
       S.gradeProduto           AS gradeProduto,
       P.unidade                AS unidade,
       S.estoqueTotal           AS estoqueTotal,
       S.estoqueDS              AS estoqueDS,
       S.estoqueMR              AS estoqueMR,
       S.estoqueMF              AS estoqueMF,
       S.estoquePK              AS estoquePK,
       S.estoqueTM              AS estoqueTM,
       P.tributacao             AS tributacao,
       P.rotulo                 AS rotulo,
       P.ncm                    AS ncm,
       P.fornecedor             AS fornecedor,
       P.tipo                   AS tipo,
       P.cl                     AS cl,
       MID(L.localizacao, 1, 4) AS localizacao,
       R.prdnoRel               AS prdnoRel,
       TRIM(R.prdnoRel) * 1     AS codigoRel
FROM
  T_STK              AS S
    LEFT JOIN  T_LOC AS L
               USING (prdno, gradeProduto)
    INNER JOIN T_PRD AS P
               USING (prdno)
    LEFT JOIN  T_REL AS R
               USING (prdno, temRelacionado)
WHERE ((:estoque = '<' AND S.estoqueTotal < :saldo) OR (:estoque = '>' AND S.estoqueTotal > :saldo) OR
       (:estoque = '=' AND S.estoqueTotal = :saldo) OR (:estoque = 'T'));

SELECT prdno,
       codigo,
       descricao,
       gradeProduto,
       unidade,
       estoqueTotal,
       estoqueDS,
       estoqueMR,
       estoqueMF,
       estoquePK,
       estoqueTM,
       tributacao,
       rotulo,
       ncm,
       fornecedor,
       tipo,
       cl,
       localizacao,
       prdnoRel,
       codigoRel
FROM
  T_PRDSTK AS S
WHERE (@PESQUISA = '' OR codigo = @PESQUISA OR descricao LIKE @PESQUISA_LIKE OR gradeProduto LIKE @PESQUISA_START OR
       unidade = @PESQUISA OR ncm LIKE @PESQUISA_START OR fornecedor = @PESQUISA OR cl = @PESQUISA)