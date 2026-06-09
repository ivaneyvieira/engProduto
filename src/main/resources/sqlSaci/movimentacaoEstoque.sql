USE sqldados;

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
  AND noRota IN (0, 1, 42, 43, 45, 48);

/*Loado da loja*/
DROP TEMPORARY TABLE IF EXISTS T_MOVIMENTACAO_KARDEC;
CREATE TEMPORARY TABLE T_MOVIMENTACAO_KARDEC
(tipo varchar(30))
SELECT 4                                                           AS loja,
       prdno,
       grade,
       data,
       numloja                                                     AS ljDoc,
       CONCAT(numero, '/', numloja)                                AS doc,
       ''                                                          AS nfEnt,
       IF(noRota = 0, 'REPOSICAO_CDLJ', 'REPOSICAO_LJCD')          AS tipo,
       IF(noRota = 1, CONCAT('da\tLoja\t', numero), 'para\to\tCD') AS observacao,
       NULL                                                        AS vencimento,
       IF(noRota = 1, movimentacao, -movimentacao)                 AS qtde,
       0                                                           AS saldo,
       NULL                                                        AS userLogin,
       ER.login                                                    AS recLogin,
       EE.login                                                    AS entLogin
FROM
  T_MOVIMENTACAO_ESTOQUE     AS E
    LEFT JOIN sqldados.users AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.users AS EE
              ON EE.no = E.noEntregue
WHERE noRota IN (0, 1)
  AND E.numloja = 4;

INSERT INTO T_MOVIMENTACAO_KARDEC(loja, prdno, grade, data, ljDoc, doc, nfEnt, tipo, observacao, vencimento, qtde,
                                  saldo, userLogin, recLogin, entLogin)
SELECT E.numloja                                                   AS loja,
       prdno,
       grade,
       data,
       numloja                                                     AS ljDoc,
       CONCAT(numero, '/', numloja)                                AS doc,
       ''                                                          AS nfEnt,
       IF(noRota = 0, 'REPOSICAO_CDLJ', 'REPOSICAO_LJCD')          AS tipo,
       IF(noRota = 1, CONCAT('da\tLoja\t', numero), 'para\to\tCD') AS observacao,
       NULL                                                        AS vencimento,
       IF(noRota = 1, movimentacao, -movimentacao)                 AS qtde,
       0                                                           AS saldo,
       NULL                                                        AS userLogin,
       ER.login                                                    AS recLogin,
       EE.login                                                    AS entLogin
FROM
  T_MOVIMENTACAO_ESTOQUE     AS E
    LEFT JOIN sqldados.users AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.users AS EE
              ON EE.no = E.noEntregue
WHERE noRota IN (0, 1)
  AND E.numloja != 4;

INSERT INTO T_MOVIMENTACAO_KARDEC(loja, prdno, grade, data, ljDoc, doc, nfEnt, tipo, observacao, vencimento, qtde,
                                  saldo, userLogin, recLogin, entLogin)
SELECT CASE noRota
         WHEN 42 THEN 2
         WHEN 43 THEN 3
         WHEN 45 THEN 5/*4*/
         WHEN 48 THEN 8/*5*/
                 ELSE numloja
       END                                                         AS loja,
       prdno,
       grade,
       data,
       numloja                                                     AS ljDoc,
       CONCAT(numero, '/', numloja)                                AS doc,
       ''                                                          AS nfEnt,
       CASE noRota
         WHEN 42 THEN 'REPOSICAO_CDLJ2'
         WHEN 43 THEN 'REPOSICAO_CDLJ3'
         WHEN 45 THEN 'REPOSICAO_CDLJ5'
         WHEN 48 THEN 'REPOSICAO_CDLJ8'
                 ELSE CONCAT('REPOSICAO_CDLJ', numloja)
       END                                                         AS tipo,
       IF(noRota = 1, CONCAT('da\tLoja\t', numero), 'para\to\tCD') AS observacao,
       NULL                                                        AS vencimento,
       IF(noRota = 1, movimentacao, -movimentacao)                 AS qtde,
       0                                                           AS saldo,
       NULL                                                        AS userLogin,
       ER.login                                                    AS recLogin,
       EE.login                                                    AS entLogin
FROM
  T_MOVIMENTACAO_ESTOQUE     AS E
    LEFT JOIN sqldados.users AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.users AS EE
              ON EE.no = E.noEntregue
WHERE noRota IN (42, 43, 45, 48);

SELECT loja,
       prdno,
       grade,
       data,
       ljDoc,
       doc,
       nfEnt,
       tipo,
       '' AS observacao,
       vencimento,
       qtde,
       saldo,
       userLogin,
       recLogin,
       entLogin
FROM
  T_MOVIMENTACAO_KARDEC
WHERE loja = :loja

