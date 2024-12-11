USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE TEMPORARY TABLE T_PEDIDO
(
  PRIMARY KEY (prdno, grade)
)
SELECT O.storeno                             AS loja,
       O.ordno                               AS pedido,
       MID(O.ordno, 1, 1) * 1                AS lojaPedido,
       O.prdno                               AS prdno,
       TRIM(O.prdno)                         AS codigo,
       TRIM(MID(P.name, 1, 37))              AS descicao,
       TRIM(P.barcode)                       AS barcode,
       O.grade                               AS grade,
       P.mfno                                AS vendno,
       IF(P.tipoGarantia = 2, P.garantia, 0) AS validade,
       SUM(O.qtty)                           AS quant
FROM sqldados.oprd AS O
       INNER JOIN sqldados.prd AS P
                  ON P.no = O.prdno
WHERE storeno = 1
  AND ordno = :pedido
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno)
)
SELECT L.prdno, GROUP_CONCAT(DISTINCT MID(localizacao, 1, 4) ORDER BY 1) AS loc
FROM sqldados.prdloc AS L
       INNER JOIN T_PEDIDO AS P
                  ON P.prdno = L.prdno
                    AND P.grade = L.grade
                    AND L.storeno = 4
WHERE localizacao REGEXP 'CD[0-9][A-Z]'
GROUP BY prdno;

DROP TEMPORARY TABLE IF EXISTS T_LOCA;
CREATE TEMPORARY TABLE T_LOCA
(
  PRIMARY KEY (prdno)
)
SELECT L.prdno, GROUP_CONCAT(DISTINCT MID(localizacao, 1, 4) ORDER BY 1) AS loc
FROM sqldados.prdAdicional AS L
       INNER JOIN T_PEDIDO AS P
                  ON P.prdno = L.prdno
                    AND P.grade = L.grade
                    AND L.storeno = 4
WHERE localizacao REGEXP 'CD[0-9][A-Z]'
GROUP BY prdno;



DROP TEMPORARY TABLE IF EXISTS T_GRADE;
CREATE TEMPORARY TABLE T_GRADE
(
  PRIMARY KEY (prdno, grade)
)
SELECT B.prdno, B.grade, MAX(IF(LENGTH(TRIM(B.barcode)) >= 13, TRIM(B.barcode), '')) AS barcode
FROM sqldados.prdbar AS B
       INNER JOIN T_PEDIDO AS P
                  ON P.prdno = B.prdno
                    AND P.grade = B.grade
                    AND B.grade != ''
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ESTOQUE;
CREATE TEMPORARY TABLE T_ESTOQUE
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT S.storeno, S.prdno, S.grade, SUM(S.qtty_atacado + S.qtty_varejo) / 1000 AS estoque
FROM sqldados.stk AS S
       INNER JOIN T_PEDIDO AS P
                  ON P.prdno = S.prdno
                    AND P.grade = S.grade
WHERE S.storeno IN (2, 3, 4, 5, 8)
GROUP BY storeno, prdno, grade;

SELECT P.loja                       AS loja,
       P.pedido                     AS pedido,
       P.prdno                      AS prdno,
       P.codigo                     AS codigo,
       IFNULL(G.barcode, P.barcode) AS barcode,
       P.descicao                   AS descricao,
       P.grade                      AS grade,
       P.vendno                     AS vendno,
       IFNULL(LA.loc, L.loc)        AS localizacao,
       P.validade                   AS validade,
       P.quant                      AS qtPedido,
       ROUND(E.estoque, 2)          AS estoque
FROM T_PEDIDO AS P
       LEFT JOIN T_LOC AS L
                 ON P.prdno = L.prdno
       LEFT JOIN T_LOCA AS LA
                 ON P.prdno = LA.prdno
       LEFT JOIN T_GRADE AS G
                 ON P.prdno = G.prdno
                   AND P.grade = G.grade
       LEFT JOIN T_ESTOQUE AS E
                 ON P.prdno = E.prdno
                   AND P.grade = E.grade
                   AND E.storeno = P.lojaPedido
