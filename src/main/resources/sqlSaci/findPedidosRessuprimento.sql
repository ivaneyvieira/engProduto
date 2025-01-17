SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_OPRD;
CREATE TEMPORARY TABLE T_OPRD
(
    PRIMARY KEY (storeno, ordno)
)
SELECT
    storeno,
    ordno,
    SUM(qttyRcv * cost)                       AS totalRecebido,
    SUM((qtty - qttyCancel - qttyRcv) * cost) AS totalPendente,
    SUM((qtty - qttyCancel) * cost)           AS totalPedido
FROM sqldados.oprd AS P
GROUP BY
    storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_ORD;
CREATE TEMPORARY TABLE T_ORD
SELECT
    O.storeno            AS loja,
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
FROM sqldados.ords AS O INNER JOIN sqldados.store AS L
                                       ON O.storeno = L.no LEFT JOIN T_OPRD AS OP
                                       ON O.storeno = OP.storeno AND O.no = OP.ordno INNER JOIN sqldados.vend AS V
                                       ON O.vendno = V.no
WHERE
      (O.storeno = 1)
  AND ((O.no BETWEEN 20000 AND 29999) OR (O.no BETWEEN 30000 AND 39999) OR (O.no BETWEEN 40000 AND 49999) OR
       (O.no BETWEEN 50000 AND 59999) OR (O.no BETWEEN 80000 AND 89999) OR (O.no BETWEEN 200000000 AND 299999999) OR
       (O.no BETWEEN 300000000 AND 399999999) OR (O.no BETWEEN 400000000 AND 499999999) OR
       (O.no BETWEEN 500000000 AND 599999999) OR (O.no BETWEEN 800000000 AND 899999999));

SELECT
    loja,
    sigla,
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
WHERE
    (pedido = @PESQUISA_NUM OR fornecedor LIKE @PESQUISA_LIKE OR vendno = @PESQUISA_NUM OR
     observacao LIKE @PESQUISA_LIKE OR @PESQUISA = '')
ORDER BY
    data DESC, loja, pedido DESC