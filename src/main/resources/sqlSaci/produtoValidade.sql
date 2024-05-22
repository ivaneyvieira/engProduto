USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISALIKE := IF(@PESQUISA REGEXP '[0-9]+', '', CONCAT('%', @PESQUISA, '%'));
DO @CODIGO := :codigo;
DO @PRDNO := LPAD(@CODIGO, 16, ' ');
DO @VALIDADE := :validade;
DO @GRADE := :grade;
DO @CARACTER := :caracter;

DROP TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT P.no                              AS prdno,
       TRIM(P.no)                        AS codigo,
       TRIM(MID(P.name, 1, 37))          AS descricao,
       IFNULL(B.grade, '')               AS grade,
       TRIM(MID(P.name, 37, 3))          AS unidade,
       IF(tipoGarantia = 2, garantia, 0) AS validade
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdbar AS B
                 ON P.no = B.prdno
WHERE IF(tipoGarantia = 2, garantia, 0) > 0
  AND (P.no = @PRDNO OR @CODIGO = '')
  AND (IF(tipoGarantia = 2, garantia, 0) = @VALIDADE OR @VALIDADE = 0)
  AND (B.grade = @GRADE OR @GRADE = '')
  AND CASE :caracter
        WHEN 'S' THEN P.name NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN P.name REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END
GROUP BY prdno, TRIM(MID(P.name, 1, 37));

DROP TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, ROUND(SUM(qtty_varejo + stk.qtty_atacado) / 1000) AS estoqueTotal
FROM sqldados.stk
       INNER JOIN T_PRD
                  USING (prdno, grade)
WHERE storeno IN (2, 3, 4, 5, 8)
GROUP BY prdno, grade;

DROP TABLE IF EXISTS T_VAL;
CREATE TEMPORARY TABLE T_VAL
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno                               AS prdno,
       grade                               AS grade,
       SUM(IF(storeno = 2, estoque, 0))    AS estoqueDS,
       SUM(IF(storeno = 3, estoque, 0))    AS estoqueMR,
       SUM(IF(storeno = 4, estoque, 0))    AS estoqueMF,
       SUM(IF(storeno = 5, estoque, 0))    AS estoquePK,
       SUM(IF(storeno = 8, estoque, 0))    AS estoqueTM,
       SUM(IF(storeno = 2, vencimento, 0)) AS vencimentoDS,
       SUM(IF(storeno = 3, vencimento, 0)) AS vencimentoMR,
       SUM(IF(storeno = 4, vencimento, 0)) AS vencimentoMF,
       SUM(IF(storeno = 5, vencimento, 0)) AS vencimentoPK,
       SUM(IF(storeno = 8, vencimento, 0)) AS vencimentoTM
FROM sqldados.produtoValidade
       INNER JOIN T_PRD
                  USING (prdno, grade)
GROUP BY prdno, grade;

SELECT P.prdno,
       P.codigo,
       P.descricao,
       grade,
       P.unidade,
       P.validade,
       IFNULL(S.estoqueTotal, 0) AS estoqueTotal,
       V.estoqueDS,
       V.estoqueMR,
       V.estoqueMF,
       V.estoquePK,
       V.estoqueTM,
       DATE(V.vencimentoDS)      AS vencimentoDS,
       DATE(V.vencimentoMR)      AS vencimentoMR,
       DATE(V.vencimentoMF)      AS vencimentoMF,
       DATE(V.vencimentoPK)      AS vencimentoPK,
       DATE(V.vencimentoTM)      AS vencimentoTM
FROM T_PRD AS P
       LEFT JOIN T_STK AS S
                 USING (prdno, grade)
       LEFT JOIN T_VAL AS V
                 USING (prdno, grade)
WHERE (@PESQUISA = '' OR
       descricao LIKE @PESQUISALIKE OR
       unidade = @PESQUISA)
GROUP BY prdno, grade, descricao, unidade

