SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

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
       IFNULL(I01.invno, I02.invno)                           AS invno
FROM sqldados.ords AS O
       INNER JOIN sqldados.oprd AS I
                  ON O.storeno = I.storeno
                    AND O.no = I.ordno
       INNER JOIN sqldados.vend AS V
                  ON O.vendno = V.no
       INNER JOIN sqldados.prd AS P
                  ON P.no = I.prdno
       LEFT JOIN sqldados.inv AS I01
                 ON I01.invno = O.invno
       LEFT JOIN sqldados.inv AS I02
                 ON I02.ordno = I.ordno
                   AND I02.storeno = I.storeno
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
       invno                          AS invno,
       CAST(I.issue_date AS DATE)     AS dataEmissao,
       CAST(I.date AS DATE)           AS dataEntrada,
       CONCAT(I.nfname, '/', I.invse) AS nfEntrada
FROM T_ORD AS O
       LEFT JOIN sqldados.inv AS I
                 USING (invno)
WHERE pedido = @PESQUISA_NUM
   OR fornecedor LIKE @PESQUISA
   OR no = @PESQUISA_NUM
   OR @PESQUISA = ''
ORDER BY data DESC, storeno, pedido DESC, invno, prdno, grade