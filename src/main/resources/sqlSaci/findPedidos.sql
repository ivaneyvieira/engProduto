SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP TEMPORARY TABLE IF EXISTS T_INV1;
CREATE TEMPORARY TABLE T_INV1
(
  PRIMARY KEY (storeno, ordno)
)
SELECT O.storeno      AS storeno,
       O.no           AS ordno,
       O.vendno      AS vendno,
       MAX(I02.invno) AS invnoMax
FROM sqldados.ords AS O
       INNER JOIN sqldados.inv AS I02
                  ON I02.ordno = O.no
                    AND I02.storeno = O.storeno
GROUP BY O.storeno, O.no;

DROP TEMPORARY TABLE IF EXISTS T_INV2;
CREATE TEMPORARY TABLE T_INV2
(
  PRIMARY KEY (storeno, ordno)
)
SELECT O.storeno      AS storeno,
       O.no           AS ordno,
       O.vendno      AS vendno,
       MAX(I02.invno) AS invnoMax
FROM sqldados.ords AS O
       INNER JOIN sqldados.inv2 AS I02
                  ON I02.ordno = O.no
                    AND I02.storeno = O.storeno
GROUP BY O.storeno, O.no;

DROP TEMPORARY TABLE IF EXISTS T_ORD;
CREATE TEMPORARY TABLE T_ORD
SELECT O.storeno                                              AS loja,
       O.no                                                   AS pedido,
       CAST(O.date AS DATE)                                   AS data,
       O.status                                               AS status,
       O.vendno                                               AS no,
       V.name                                                 AS fornecedor,
       O.amt / 100                                            AS total,
       TRIM(I.prdno)                                          AS codigo,
       I.prdno                                                AS prdno,
       TRIM(MID(P.name, 1, 37))                               AS descricao,
       I.grade                                                AS grade,
       ROUND(I.qtty)                                          AS qtty,
       ROUND(I.qttyCancel)                                    AS qttyCancel,
       ROUND(I.qttyRcv)                                       AS qttyRcv,
       ROUND(I.qtty - I.qttyCancel - I.qttyRcv)               AS qttyPendente,
       I.cost                                                 AS custo,
       ROUND(I.qtty * I.cost, 2)                              AS totalProduto,
       ROUND((I.qtty - I.qttyCancel - I.qttyRcv) * I.cost, 2) AS totalProdutoPendente,
       I01.invnoMAX                                           AS invno,
       I02.invnoMAX                                           AS invno2
FROM sqldados.ords AS O
       INNER JOIN sqldados.oprd AS I
                  ON O.storeno = I.storeno
                    AND O.no = I.ordno
       INNER JOIN sqldados.vend AS V
                  ON O.vendno = V.no
       INNER JOIN sqldados.prd AS P
                  ON P.no = I.prdno
       LEFT JOIN T_INV1 AS I01
                 ON I01.ordno = O.no
                   AND I01.storeno = O.storeno
                   AND I01.vendno = O.vendno
       LEFT JOIN T_INV2 AS I02
                 ON I02.ordno = O.no
                   AND I02.storeno = O.storeno
                   AND I02.vendno = O.vendno
WHERE V.name NOT LIKE 'ENGECOPI%'
  AND (O.storeno = :loja OR :loja = 0)
  AND (O.date >= :dataInicial OR :dataInicial = 0)
  AND (O.date <= :dataFinal OR :dataFinal = 0)
  AND I.status != 2
  AND ((:status = 0 AND ROUND((I.qtty - I.qttyCancel - I.qttyRcv) * I.cost, 2) > 0)
  OR (:status = 1 AND ROUND((I.qtty - I.qttyCancel - I.qttyRcv) * I.cost, 2) = 0)
  OR (:status = 999))
GROUP BY loja, pedido, invno, prdno, grade;


SELECT loja,
       pedido,
       data,
       status,
       no,
       fornecedor,
       total,
       codigo,
       prdno,
       descricao,
       grade,
       qtty,
       qttyCancel,
       qttyRcv,
       qttyPendente,
       custo,
       totalProduto,
       totalProdutoPendente,
       I.invno                                                                  AS invno,
       CAST(IFNULL(I.issue_date, I2.issue_date) AS DATE)                        AS dataEmissao,
       CAST(I.date AS DATE)                                                     AS dataEntrada,
       IFNULL(CONCAT(I.nfname, '/', I.invse), CONCAT(I2.nfname, '/', I2.invse)) AS nfEntrada
FROM T_ORD AS O
       LEFT JOIN sqldados.inv AS I
                 USING (invno)
       LEFT JOIN sqldados.inv2 AS I2
                 ON I2.invno = O.invno2
WHERE pedido = @PESQUISA_NUM
   OR fornecedor LIKE @PESQUISA
   OR no = @PESQUISA_NUM
   OR @PESQUISA = ''
ORDER BY data DESC, loja, pedido DESC, invno, prdno, grade