SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

SELECT O.storeno                        AS loja,
       O.no                             AS pedido,
       CAST(O.date AS DATE)             AS data,
       O.status                         AS status,
       O.vendno                         AS no,
       V.name                           AS fornecedor,
       O.amt / 100                      AS total,
       TRIM(I.prdno)                    AS codigo,
       I.prdno                          AS prdno,
       TRIM(MID(P.name, 1, 37))         AS descricao,
       I.qtty / 1000                    AS qtty,
       I.cost / 10000                   AS custo,
       (I.qtty / 1000) * I.cost / 10000 AS totalProduto
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
  AND (I.status = :status OR :status = 999)
HAVING pedido = @PESQUISA_NUM
    OR fornecedor LIKE @PESQUISA
    OR no = @PESQUISA_NUM
    OR @PESQUISA = ''
ORDER BY O.storeno, O.no
