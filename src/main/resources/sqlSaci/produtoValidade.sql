USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISALIKE := IF(@PESQUISA NOT REGEXP '[0-9]+', CONCAT('%', @PESQUISA, '%'), '');
DO @CODIGO := :codigo;
DO @PRDNO := LPAD(@CODIGO, 16, ' ');
DO @VALIDADE := :validade;
DO @GRADE := :grade;
DO @CARACTER := :caracter;

DROP TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT S.no                              AS storeno,
       S.sname                           AS abrevLoja,
       P.no                              AS prdno,
       TRIM(P.no) * 1                    AS codigo,
       TRIM(MID(P.name, 1, 37))          AS descricao,
       IFNULL(B.grade, '')               AS grade,
       TRIM(MID(P.name, 37, 3))          AS unidade,
       IF(tipoGarantia = 2, garantia, 0) AS validade,
       P.mfno                            AS vendno,
       V.sname                           AS fornecedorAbrev
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdbar AS B
                 ON P.no = B.prdno
       LEFT JOIN sqldados.vend AS V
                 ON V.no = P.mfno
       INNER JOIN sqldados.store AS S
                  ON S.no IN (2, 3, 4, 5, 8)
                    AND (S.no = :loja OR :loja = 0)
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
GROUP BY S.no, prdno, TRIM(MID(P.name, 1, 37)), grade;

DROP TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno,
       prdno,
       grade,
       ROUND(SUM(qtty_varejo + stk.qtty_atacado) / 1000) AS estoqueLoja
FROM sqldados.stk
       INNER JOIN T_PRD
                  USING (storeno, prdno, grade)
WHERE storeno IN (2, 3, 4, 5, 8)
  AND (:loja = 0 OR storeno = :loja)
GROUP BY storeno, prdno, grade;

DROP TABLE IF EXISTS T_STK_TOTAL;
CREATE TEMPORARY TABLE T_STK_TOTAL
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM(estoqueLoja) AS estoqueTotal
FROM T_STK
GROUP BY prdno, grade;

DROP TABLE IF EXISTS T_VAL;
CREATE TEMPORARY TABLE T_VAL
(
  PRIMARY KEY (storeno, prdno, grade, vencimento, tipo, dataEntrada)
)
SELECT storeno     AS storeno,
       prdno       AS prdno,
       grade       AS grade,
       dataEntrada AS dataEntrada,
       vencimento  AS vencimento,
       tipo        AS tipo,
       movimento   AS movimento
FROM sqldados.produtoValidade
       INNER JOIN T_PRD
                  USING (storeno, prdno, grade)
WHERE (:ano = 0 OR MID(vencimento, 1, 4) = :ano)
  AND (:mes = 0 OR MID(vencimento, 5, 6) = :mes)
  AND (:loja = 0 OR storeno = :loja)
  AND (movimento != 0);

SELECT P.storeno                                              AS loja,
       P.abrevLoja                                            AS lojaAbrev,
       P.prdno                                                AS prdno,
       P.codigo                                               AS codigo,
       P.descricao                                            AS descricao,
       P.grade                                                AS grade,
       P.unidade                                              AS unidade,
       P.validade                                             AS validade,
       P.vendno                                               AS vendno,
       P.fornecedorAbrev                                      AS fornecedorAbrev,
       CAST(IF(V.dataEntrada = 0, NULL, dataEntrada) AS DATE) AS dataEntrada,
       CAST(IF(V.dataEntrada = 0, NULL, dataEntrada) AS DATE) AS dataEntradaEdit,
       IFNULL(T.estoqueTotal, 0)                              AS estoqueTotal,
       IFNULL(S.estoqueLoja, 0)                               AS estoqueLoja,
       IFNULL(V.movimento, 0)                                 AS movimento,
       IFNULL(V.tipo, 'INV')                                  AS tipo,
       IFNULL(V.tipo, 'INV')                                  AS tipoEdit,
       MID(V.vencimento, 1, 6) * 1                            AS vencimento,
       MID(V.vencimento, 1, 6) * 1                            AS vencimentoEdit
FROM T_STK AS S
       INNER JOIN T_STK_TOTAL AS T
                  USING (prdno, grade)
       INNER JOIN T_PRD AS P
                  USING (storeno, prdno, grade)
       LEFT JOIN T_VAL AS V
                 USING (storeno, prdno, grade)
WHERE (@PESQUISA = '' OR
       descricao LIKE @PESQUISALIKE OR
       fornecedorAbrev LIKE @PESQUISALIKE OR
       unidade = @PESQUISA OR
       vendno = @PESQUISANUM)
  AND ((:ano = 0) OR (MID(vencimento, 1, 4) = :ano))
  AND ((:mes = 0) OR (MID(vencimento, 5, 6) = :mes))
GROUP BY S.storeno, prdno, codigo, grade, descricao, unidade, vencimento, V.tipo, V.dataEntrada
