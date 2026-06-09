USE sqldados;

/* Kardec Loja*/

SET SQL_MODE = '';

DO @DATA_FINAL := ROUND(CURDATE() * 1);

DROP TABLE IF EXISTS T_KARDEX;
CREATE TEMPORARY TABLE T_KARDEX
(
  tipo       VARCHAR(15),
  observacao VARCHAR(255),
  recLogin   varchar(50),
  entLogin   varchar(50)
)
SELECT X.storeno                    AS loja,
       prdno                        AS prdno,
       grade                        AS grade,
       CAST(X.date AS date)         AS data,
       SUBSTRING_INDEX(doc, '.', 1) AS doc,
       P.eordno                     AS pedido,
       'VENDA'                      AS tipo,
       ROUND(-qtty / 1000)          AS qtde,
       N.remarks                    AS observacao,
       0                            AS saldo,
       U.sname                      AS userLogin
FROM
  sqldados.xalog2           AS X
    INNER JOIN sqldados.nf  AS N
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqlpdv.pxa   AS P
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.emp AS U
               ON U.no = N.empno
WHERE prdno = :prdno
  AND grade = :grade
  AND X.storeno = :loja
  AND X.date BETWEEN :dataInicial AND @DATA_FINAL
  AND qtty > 0
  AND N.tipo = 0
  AND N.status <> 1;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, pedido, tipo, qtde, observacao, saldo, userLogin)
SELECT N.storeno                   AS loja,
       P.prdno                     AS prdno,
       P.grade                     AS grade,
       CAST(N.issuedate AS date)   AS data,
       CONCAT(N.nfno, '/', N.nfse) AS doc,
       N.eordno                    AS pedido,
       'FATURA'                    AS tipo,
       ROUND(-P.qtty)              AS qtde,
       N.remarks                   AS observacao,
       0                           AS saldo,
       U.sname                     AS userLogin
FROM
  sqldados.nf                 AS N
    INNER JOIN sqldados.xaprd AS P
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.emp   AS U
               ON U.no = N.empno
WHERE P.prdno = :prdno
  AND P.grade = :grade
  AND N.storeno = :loja
  AND N.tipo = 3
  AND N.nfse = '3'
  AND N.nfse != '3'
  AND N.issuedate BETWEEN :dataInicial AND @DATA_FINAL
  AND N.status <> 1;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, pedido, tipo, qtde, observacao, saldo, userLogin)
SELECT N.storeno                      AS loja,
       P.prdno                        AS prdno,
       P.grade                        AS grade,
       CAST(N.comp_date AS date)      AS data,
       CONCAT(N.nfname, '/', N.invse) AS doc,
       N.ordno                        AS pedido,
       'DEVOLUCAO'                    AS tipo,
       ROUND(P.qtty / 1000)           AS qtde,
       remarks                        AS observacao,
       0                              AS saldo,
       U.login                        AS userLogin
FROM
  sqldados.inv                AS N
    LEFT JOIN  sqldados.users AS U
               ON U.no = IF(N.usernoLast = 0, N.usernoFirst, N.usernoLast)
    INNER JOIN sqldados.iprd  AS P
               USING (invno)
WHERE P.prdno = :prdno
  AND P.grade = :grade
  AND N.storeno = :loja
  AND N.type = 2
  AND N.bits & POW(2, 4) = 0
  AND N.invno NOT IN ( SELECT nfNfno FROM sqldados.inv WHERE auxShort13 & POW(2, 15) != 0 )
  AND N.comp_date BETWEEN :dataInicial AND @DATA_FINAL
  AND P.prdno IN ( SELECT prdno FROM sqldados.produtos_dev_loja );


DROP TABLE IF EXISTS T_REPOSICAO;
CREATE TEMPORARY TABLE T_REPOSICAO
SELECT O.storeno                                    AS loja,
       E.prdno                                      AS prdno,
       E.grade                                      AS grade,
       CAST(O.date AS DATE)                         AS data,
       O.ordno                                      AS doc,
       O.ordno                                      AS pedido,
       ROUND(E.qtty / 1000)                         AS qtde,
       TRIM(MID(IFNULL(R.remarks__480, ''), 1, 40)) AS observacao,
       CASE O.paymno
         WHEN 431 THEN 'REPOSICAO'
         WHEN 432 THEN 'RETORNO'
         WHEN 433 THEN 'ACERTO'
                  ELSE ''
       END                                          AS metodo,
       IFNULL(EA.marca, 0)                          AS marca,
       IF(O.paymno = 433, ROUND(CASE
                                  WHEN R.remarks__480 LIKE 'ENTRADA%' THEN 1
                                  WHEN R.remarks__480 LIKE 'SAIDA%'   THEN -1
                                                                      ELSE 0
                                END), 1)            AS multAcerto,
       U.sname                                      AS userLogin
FROM
  sqldados.eoprd                       AS E
    LEFT JOIN  sqldados.eoprdAdicional AS EA
               USING (storeno, ordno, prdno, grade)
    INNER JOIN sqldados.eord           AS O
               USING (storeno, ordno)
    LEFT JOIN  sqldados.eordrk         AS R
               USING (storeno, ordno)
    LEFT JOIN  sqldados.emp            AS U
               ON U.no = O.empno
WHERE (O.paymno IN (431, 432, 433))
  AND (O.date BETWEEN :dataInicial AND @DATA_FINAL)
  AND (E.storeno = :loja)
  AND (E.prdno = :prdno)
  AND (E.grade = :grade)
GROUP BY E.storeno, E.ordno, E.prdno, E.grade
HAVING multAcerto != 0;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, pedido, tipo, qtde, observacao, saldo, userLogin)
SELECT loja,
       prdno,
       grade,
       data,
       doc,
       pedido,
       metodo            AS tipo,
       qtde * multAcerto AS qtde,
       observacao,
       0                 AS saldo,
       userLogin
FROM
  T_REPOSICAO
WHERE marca = 1
  AND metodo IN ('REPOSICAO');

DROP TEMPORARY TABLE IF EXISTS T_MOVIMENTACAO_ESTOQUE;
CREATE TEMPORARY TABLE T_MOVIMENTACAO_ESTOQUE
SELECT numero,
       numloja,
       data,
       hora,
       prdno,
       grade,
       movimentacao,
       estoque,
       noLogin,
       noEntregue,
       noRecebido,
       noGravado,
       noRota
FROM
  sqldados.produtoMovimentacao
WHERE prdno = :prdno
  AND grade = :grade
  AND (numloja = :loja OR :loja = 4)
  AND data >= :dataInicial
  AND noGravado > 0
  AND noEntregue > 0
  AND noRecebido > 0
  AND noRota IN (0, 1);

DROP TEMPORARY TABLE IF EXISTS T_MOVIMENTACAO_KARDEC;
CREATE TEMPORARY TABLE T_MOVIMENTACAO_KARDEC
(tipo varchar(30))
SELECT numloja                                                      AS loja,
       prdno,
       grade,
       data,
       CONCAT(numero, '/', numloja)                                 AS doc,
       ''                                                           AS nfEnt,
       IF(noRota = 0, 'REPOSICAO_CDLJ', 'REPOSICAO_LJCD')           AS tipo,
       IF(noRota = 0, 'do\tCD', CONCAT('para\ta\tLoja\t', numloja)) AS observacao,
       NULL                                                         AS vencimento,
       IF(noRota = 0, movimentacao, -movimentacao)                  AS qtde,
       0                                                            AS saldo,
       NULL                                                         AS userLogin,
       ER.login                                                     AS recLogin,
       EE.login                                                     AS entLogin
FROM
  T_MOVIMENTACAO_ESTOQUE     AS E
    LEFT JOIN sqldados.users AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.users AS EE
              ON EE.no = E.noEntregue
WHERE numloja = :loja;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo, recLogin, entLogin, userLogin)
SELECT loja, prdno, grade, data, doc, tipo, qtde, observacao, 0 AS saldo, recLogin, entLogin, entLogin
FROM
  T_MOVIMENTACAO_KARDEC;

DROP TEMPORARY TABLE IF EXISTS T_KARDEC_CD;
CREATE TEMPORARY TABLE T_KARDEC_CD
(
  PRIMARY KEY (loja, prdno, grade, doc)
)
SELECT loja, prdno, grade, doc
FROM
  sqldados.produtoKardec AS K
WHERE (loja = :loja OR :loja = 0)
  AND prdno = :prdno
  AND grade = :grade
  AND DATA <= CURRENT_DATE * 1
  AND tipo = 'EXPEDICAO'
GROUP BY loja, prdno, grade, doc;

SELECT loja,
       prdno,
       grade,
       data,
       doc,
       IF(pedido = 0, NULL, pedido) AS pedido,
       tipo,
       qtde,
       ''                           AS observacao,
       saldo,
       recLogin,
       entLogin,
       userLogin
FROM
  T_KARDEX                AS L
    LEFT JOIN T_KARDEC_CD AS C
              USING (loja, prdno, grade, doc)
WHERE L.doc NOT LIKE '%/3'
  AND C.doc IS NULL
ORDER BY DATA, loja, prdno, grade, doc
