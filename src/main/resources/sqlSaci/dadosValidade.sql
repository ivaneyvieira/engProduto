USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, '');
DO @PESQUISALIKE := IF(@PESQUISA NOT REGEXP '^[0-9]+$', CONCAT('%', @PESQUISA, '%'), '');
DO @CODIGO := :codigo;
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
WHERE garantia > 0
  AND tipoGarantia = 2
  AND (TRIM(P.no) = @CODIGO OR @CODIGO = '')
  AND (IF(tipoGarantia = 2, garantia, 0) = @VALIDADE OR @VALIDADE = 0)
  AND (B.grade = @GRADE OR @GRADE = '')
  AND CASE :caracter
        WHEN 'S' THEN P.name NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN P.name REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END
GROUP BY S.no, P.no, B.grade;

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

SELECT IFNULL(V.seq, 0)                                         AS seq,
       storeno                                                  AS loja,
       abrevLoja                                                AS abrevLoja,
       prdno                                                    AS prdno,
       TRIM(prdno) * 1                                          AS codigo,
       P.descricao                                              AS descricao,
       P.grade                                                  AS grade,
       IFNULL(S.estoqueLoja, 0)                                 AS estoqueLoja,
       MID(V.vencimento, 1, 6) * 1                              AS vencimento,
       V.inventario                                             AS inventario,
       CAST(IF(V.dataEntrada = 0, NULL, V.dataEntrada) AS DATE) AS dataEntrada,
       validade                                                 AS validade,
       unidade                                                  AS unidade,
       vendno                                                   AS vendno
FROM T_PRD AS P
       LEFT JOIN sqldados.dadosValidade AS V
                 USING (storeno, prdno, grade)
       LEFT JOIN T_STK AS S
                 USING (storeno, prdno, grade)
       LEFT JOIN T_STK_TOTAL AS ST
                 USING (prdno, grade)
WHERE (:ano = 0 OR MID(vencimento, 1, 4) = :ano)
  AND (:mes = 0 OR MID(vencimento, 5, 6) = :mes)
  AND (@PESQUISA = '' OR
       descricao LIKE @PESQUISALIKE OR
       vendno = @PESQUISANUM OR
       unidade = @PESQUISA)
