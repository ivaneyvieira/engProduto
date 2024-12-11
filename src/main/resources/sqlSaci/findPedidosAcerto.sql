SET SQL_MODE = '';

USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_OPRD;
CREATE TEMPORARY TABLE T_OPRD
(
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno,
       ordno,
       SUM(qttyRcv * cost)                       AS totalRecebido,
       SUM((qtty - qttyCancel - qttyRcv) * cost) AS totalPendente,
       SUM((qtty - qttyCancel) * cost)           AS totalPedido
FROM sqldados.oprd AS P
WHERE (storeno, ordno) IN (
                           (1, 2),
                           (2, 2),
                           (3, 2),
                           (5, 2),
                           (8, 2),
                           (1, 22),
                           (1, 33),
                           (1, 44),
                           (1, 55),
                           (1, 88)
  )
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_ORD;
CREATE TEMPORARY TABLE T_ORD
SELECT O.storeno            AS loja,
       IF(LENGTH(O.no) = 2,
          MID(O.no, 1, 1) * 1,
          IF(O.storeno = 1,
             0,
             O.storeno))    AS lojaPedido,
       L.sname              AS sigla,
       O.no                 AS pedido,
       CAST(O.date AS DATE) AS data,
       O.status             AS status,
       O.vendno             AS vendno,
       V.name               AS fornecedor,
       OP.totalPedido       AS totalPedido,
       O.freightAmt / 100   AS frete,
       O.remarks            AS observacao,
       OP.totalPendente     AS totalPendente
FROM sqldados.ords AS O
       INNER JOIN sqldados.store AS L
                  ON O.storeno = L.no
       INNER JOIN T_OPRD AS OP
                  ON O.storeno = OP.storeno
                    AND O.no = OP.ordno
       INNER JOIN sqldados.vend AS V
                  ON O.vendno = V.no
WHERE (O.storeno, O.no) IN (
                            (1, 2),
                            (2, 2),
                            (3, 2),
                            (5, 2),
                            (8, 2),
                            (1, 22),
                            (1, 33),
                            (1, 44),
                            (1, 55),
                            (1, 88)
  );

SELECT loja,
       sigla,
       lojaPedido,
       pedido,
       data,
       status,
       vendno,
       fornecedor,
       totalPedido,
       frete,
       totalPendente,
       observacao
FROM T_ORD
ORDER BY pedido, loja