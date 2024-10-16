SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP TEMPORARY TABLE IF EXISTS T_INVN;
CREATE TEMPORARY TABLE T_INVN
(
  PRIMARY KEY (invno, prdno, grade)
)
SELECT O.storeno                      AS storeno,
       O.no                           AS ordno,
       O.vendno                       AS vendno,
       I.invno                        AS invno,
       CAST(I.issue_date AS DATE)     AS dataEmissao,
       CAST(I.date AS DATE)           AS dataEntrada,
       CONCAT(I.nfname, '/', I.invse) AS nfEntrada,
       P.prdno                        AS prdno,
       P.grade                        AS grade,
       ROUND(P.qtty / 1000)           AS qtty,
       P.cost4 / 10000                AS cost
FROM sqldados.iprd AS P
       INNER JOIN sqldados.inv AS I
                  USING (invno)
       INNER JOIN sqldados.ords AS O
                  ON I.ordno = O.no
                    AND I.storeno = O.storeno
                    AND I.vendno = O.vendno
GROUP BY P.invno, P.prdno, P.grade;



DROP TEMPORARY TABLE IF EXISTS T_INVP;
CREATE TEMPORARY TABLE T_INVP
(
  PRIMARY KEY (invno, prdno, grade)
)
SELECT O.storeno                      AS storeno,
       O.no                           AS ordno,
       O.vendno                       AS vendno,
       I.invno                        AS invno,
       CAST(I.issue_date AS DATE)     AS dataEmissao,
       CAST(I.date AS DATE)           AS dataEntrada,
       CONCAT(I.nfname, '/', I.invse) AS nfEntrada,
       P.prdno                        AS prdno,
       P.grade                        AS grade,
       ROUND(P.qtty / 1000)           AS qtty,
       P.cost4 / 10000                AS cost
FROM sqldados.iprd2 AS P
       INNER JOIN sqldados.inv2 AS I
                  USING (invno)
       INNER JOIN sqldados.ords AS O
                  ON I.ordno = O.no
                    AND I.storeno = O.storeno
                    AND I.vendno = O.vendno
GROUP BY P.invno, P.prdno, P.grade;

DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV
SELECT storeno,
       ordno,
       vendno,
       invno,
       dataEmissao,
       dataEntrada,
       nfEntrada,
       prdno,
       grade,
       qtty,
       cost,
       'N' AS tipo
FROM T_INVN
UNION
DISTINCT
SELECT storeno,
       ordno,
       vendno,
       invno,
       dataEmissao,
       dataEntrada,
       nfEntrada,
       prdno,
       grade,
       qtty,
       cost,
       'P' AS tipo
FROM T_INVP;

DROP TEMPORARY TABLE IF EXISTS T_INVORD;
CREATE TEMPORARY TABLE T_INVORD
SELECT storeno,
       ordno,
       vendno,
       invno                       AS ni,
       IF(tipo = 'N', invno, NULL) AS invno,
       IF(tipo = 'P', invno, NULL) AS invno2,
       dataEmissao,
       dataEntrada,
       nfEntrada,
       prdno,
       grade,
       SUM(qtty)                   AS qtty,
       SUM(IF(tipo = 'R', qtty, 0))   qttyRcv,
       0                           AS qttyCancel,
       cost,
       tipo
FROM T_INV
WHERE (:preEntrada != 'N' OR tipo = 'P')
GROUP BY storeno, ordno, vendno, ni, prdno, grade, tipo;

DROP TEMPORARY TABLE IF EXISTS T_ORD;
CREATE TEMPORARY TABLE T_ORD
SELECT O.storeno                                                  AS loja,
       O.no                                                       AS pedido,
       CAST(O.date AS DATE)                                       AS data,
       O.status                                                   AS status,
       O.vendno                                                   AS no,
       V.name                                                     AS fornecedor,
       O.amt / 100                                                AS total,
       TRIM(IO.prdno)                                             AS codigo,
       IO.prdno                                                   AS prdno,
       TRIM(MID(P.name, 1, 37))                                   AS descricao,
       IO.grade                                                   AS grade,
       ROUND(IO.qtty)                                             AS qtty,
       ROUND(IO.qttyCancel)                                       AS qttyCancel,
       ROUND(IO.qttyRcv)                                          AS qttyRcv,
       ROUND(IO.qtty - IO.qttyCancel - IO.qttyRcv)                AS qttyPendente,
       IO.cost                                                    AS custo,
       ROUND(IO.qtty * IO.cost, 2)                                AS totalProduto,
       ROUND((IO.qtty - IO.qttyCancel - IO.qttyRcv) * IO.cost, 2) AS totalProdutoPendente,
       IO.invno                                                   AS invno,
       IO.invno2                                                  AS invno2,
       tipo                                                       AS tipo,
       dataEmissao                                                AS dataEmissao,
       dataEntrada                                                AS dataEntrada,
       nfEntrada                                                  AS nfEntrada
FROM sqldados.ords AS O
       INNER JOIN sqldados.vend AS V
                  ON O.vendno = V.no
       LEFT JOIN T_INVORD AS IO
                 ON IO.ordno = O.no
                   AND IO.storeno = O.storeno
                   AND IO.vendno = O.vendno
       LEFT JOIN sqldados.prd AS P
                 ON P.no = IO.prdno
WHERE V.name NOT LIKE 'ENGECOPI%'
  AND (O.storeno = :loja OR :loja = 0)
  AND (O.date >= :dataInicial OR :dataInicial = 0)
  AND (O.date <= :dataFinal OR :dataFinal = 0)
  AND (O.status != 2)
  AND ((:status = 0 AND ROUND((IO.qtty - IO.qttyCancel - IO.qttyRcv) * IO.cost, 2) > 0)
  OR (:status = 1 AND ROUND((IO.qtty - IO.qttyCancel - IO.qttyRcv) * IO.cost, 2) = 0)
  OR (:status = 999)
  OR (IO.storeno IS NULL));

SELECT loja,
       pedido,
       data,
       status,
       no,
       fornecedor,
       total,
       IFNULL(codigo, '')                 AS codigo,
       IFNULL(prdno, '')                  AS prdno,
       IFNULL(descricao, '')              AS descricao,
       IFNULL(grade, '')                  AS grade,
       IFNULL(qtty, 0)                    AS qtty,
       IFNULL(qttyCancel, 0)              AS qttyCancel,
       IFNULL(qttyRcv, 0)                 AS qttyRcv,
       IFNULL(qttyPendente, 0)            AS qttyPendente,
       IFNULL(custo, 0)                   AS custo,
       IFNULL(totalProduto, 0.00)         AS totalProduto,
       IFNULL(totalProdutoPendente, 0.00) AS totalProdutoPendente,
       IFNULL(invno, 0)                   AS invno,
       dataEmissao                        AS dataEmissao,
       dataEntrada                        AS dataEntrada,
       IFNULL(nfEntrada, '')              AS nfEntrada,
       IFNULL(tipo, '')                   AS tipo
FROM T_ORD
WHERE (pedido = @PESQUISA_NUM
  OR fornecedor LIKE @PESQUISA
  OR no = @PESQUISA_NUM
  OR @PESQUISA = '')
  AND (((:preEntrada = 'S') AND (tipo = 'P')) OR
       ((:preEntrada = 'N') AND (invno2 IS NULL)) OR
       (:preEntrada = ''))
ORDER BY data DESC, loja, pedido DESC, invno, prdno, grade