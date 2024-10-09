SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

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
       ROUND((I.qtty - I.qttyCancel - I.qttyRcv) * I.cost, 2) AS totalProdutoPendente
FROM sqldados.ords AS O
       INNER JOIN sqldados.oprd AS I
                  ON O.storeno = I.storeno
                    AND O.no = I.ordno
       INNER JOIN sqldados.vend AS V
                  ON O.vendno = V.no
       INNER JOIN sqldados.prd AS P
                  ON P.no = I.prdno
WHERE V.name NOT LIKE 'ENGECOPI%'
  AND (O.storeno = :loja OR :loja = 0)
  AND (O.date >= :dataInicial OR :dataInicial = 0)
  AND (O.date <= :dataFinal OR :dataFinal = 0)
  AND I.status != 2
  AND ((:status = 0 AND ROUND((I.qtty - I.qttyCancel - I.qttyRcv) * I.cost, 2) > 0)
    OR (:status = 1 AND ROUND((I.qtty - I.qttyCancel - I.qttyRcv) * I.cost, 2) = 0)
    OR (:status = 999))
HAVING pedido = @PESQUISA_NUM
    OR fornecedor LIKE @PESQUISA
    OR no = @PESQUISA_NUM
    OR @PESQUISA = ''
ORDER BY O.storeno, O.no
