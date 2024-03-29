USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT P.no                     AS prdno,
       TRIM(P.no) * 1           AS codigo,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       TRIM(MID(P.name, 38, 3)) AS unidade,
       P.taxno                  AS tributacao,
       IFNULL(R.form_label, '') AS rotulo,
       N.ncm                    AS ncm,
       P.mfno                   AS fornecedor,
       V.sname                  AS abrev,
       P.typeno                 AS tipo,
       P.clno                   AS cl
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdalq AS R
                 ON R.prdno = P.no
       LEFT JOIN sqldados.spedprd AS N
                 ON N.prdno = P.no
       LEFT JOIN sqldados.vend AS V
                 ON V.no = P.mfno
WHERE (P.mfno = :fornecedor OR :fornecedor = 0)
  AND (P.taxno = :tributacao OR :tributacao = 0)
  AND (R.form_label = :rotulo OR :rotulo = '')
  AND (P.typeno = :tipo OR :tipo = 0)
  AND (P.clno = :cl OR :cl = 0)
  AND CASE :caracter
        WHEN 'S' THEN P.name NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN P.name REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END
  AND CASE :letraDup
        WHEN 'S' THEN SUBSTRING_INDEX(P.name, ' ', 1) REGEXP 'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ'
        WHEN 'N' THEN SUBSTRING_INDEX(P.name, ' ', 1) NOT REGEXP 'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ'
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END;

DROP TEMPORARY TABLE IF EXISTS T_STKLOJA;
CREATE TEMPORARY TABLE T_STKLOJA
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT prdno                                  AS prdno,
       IF(:grade = 'S', grade, '')            AS gradeProduto,
       SUM(qtty_varejo + qtty_atacado) / 1000 AS estoqueLojas
FROM sqldados.stk
       INNER JOIN T_PRD
                  USING (prdno)
WHERE storeno IN (2, 3, 4, 5, 8)
GROUP BY prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_PRDSTK;
CREATE TEMPORARY TABLE T_PRDSTK
(
  PRIMARY KEY (loja, prdno, gradeProduto)
)
SELECT S.storeno                                         AS loja,
       S.prdno                                           AS prdno,
       P.codigo                                          AS codigo,
       P.descricao                                       AS descricao,
       IF(:grade = 'S', S.grade, '')                     AS gradeProduto,
       P.unidade                                         AS unidade,
       SUM(S.qtty_varejo / 1000)                         AS qttyVarejo,
       SUM(S.qtty_atacado / 1000)                        AS qttyAtacado,
       SUM(S.qtty_varejo / 1000 + S.qtty_atacado / 1000) AS qttyTotal,
       P.tributacao                                      AS tributacao,
       P.rotulo                                          AS rotulo,
       P.ncm                                             AS ncm,
       P.fornecedor                                      AS fornecedor,
       P.abrev                                           AS abrev,
       P.tipo                                            AS tipo,
       P.cl                                              AS cl
FROM sqldados.stk AS S
       INNER JOIN T_PRD AS P
                  USING (prdno)
WHERE (S.storeno = :loja OR :loja = 0)
GROUP BY loja, prdno, gradeProduto
HAVING CASE :estoque
         WHEN '<' THEN SUM(S.qtty_varejo + S.qtty_atacado) < :saldo
         WHEN '>' THEN SUM(S.qtty_varejo + S.qtty_atacado) > :saldo
         WHEN '=' THEN SUM(S.qtty_varejo + S.qtty_atacado) = :saldo
         WHEN 'T' THEN TRUE
         ELSE FALSE
       END;

SELECT loja,
       prdno,
       codigo,
       descricao,
       gradeProduto,
       unidade,
       L.estoqueLojas,
       qttyVarejo,
       qttyAtacado,
       qttyTotal,
       tributacao,
       rotulo,
       ncm,
       fornecedor,
       abrev,
       tipo,
       cl
FROM T_PRDSTK AS S
       LEFT JOIN T_STKLOJA AS L
                 USING (prdno, gradeProduto)
WHERE (@PESQUISA = '' OR
       codigo = @PESQUISA OR
       descricao LIKE @PESQUISA_LIKE OR
       gradeProduto LIKE @PESQUISA_START OR
       unidade = @PESQUISA OR
       tributacao = @PESQUISA OR
       rotulo LIKE @PESQUISA_START OR
       ncm LIKE @PESQUISA_START OR
       fornecedor = @PESQUISA OR
       abrev LIKE @PESQUISA_LIKE OR
       cl = @PESQUISA)