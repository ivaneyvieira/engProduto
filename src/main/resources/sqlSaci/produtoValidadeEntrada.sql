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

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
SELECT N.storeno                             AS storeno,
       I.prdno                               AS prdno,
       I.grade                               AS grade,
       CAST(N.date AS DATE)                  AS date,
       DATE_FORMAT(A.vencimento, '%Y%m') * 1 AS mesAno,
       ROUND(SUM(I.qtty / 1000))             AS qtty
FROM sqldados.iprd AS I
       INNER JOIN sqldados.inv AS N
                  USING (invno)
       INNER JOIN sqldados.iprdAdicional AS A
                  ON A.invno = I.invno
                    AND A.prdno = I.prdno
                    AND A.grade = I.grade
WHERE N.dataSaida >= :dataInicial
  AND N.storeno IN (2, 3, 4, 5, 8)
  AND (N.type = 0)
  AND (N.bits & POW(2, 4) = 0)
GROUP BY N.storeno, I.prdno, I.grade, N.date, mesAno;

DROP TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno,
       prdno,
       grade,
       ROUND(SUM(qtty_varejo + stk.qtty_atacado) / 1000) AS estoqueSaci
FROM sqldados.stk
       INNER JOIN T_PRD
                  USING (storeno, prdno, grade)
WHERE storeno IN (2, 3, 4, 5, 8)
GROUP BY storeno, prdno, grade;

DROP TABLE IF EXISTS T_STK_TOTAL;
CREATE TEMPORARY TABLE T_STK_TOTAL
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM(estoqueSaci) AS estoqueTotal
FROM T_STK
GROUP BY prdno, grade;

SELECT P.storeno       AS loja,
       P.abrevLoja     AS lojaAbrev,
       prdno           AS prdno,
       codigo          AS codigo,
       descricao       AS descricao,
       unidade         AS unidade,
       validade        AS validade,
       vendno          AS vendno,
       fornecedorAbrev AS fornecedorAbrev,
       estoqueTotal    AS estoqueTotal,
       P.grade         AS grade,
       date            AS date,
       mesAno          AS mesAno,
       qtty            AS qtty
FROM T_NOTA
       INNER JOIN T_PRD AS P
                  USING (storeno, prdno, grade)
       LEFT JOIN T_STK_TOTAL AS S
                 USING (prdno, grade)