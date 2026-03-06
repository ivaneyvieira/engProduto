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
SELECT 4                                                  AS loja,
       prdno,
       grade,
       data,
       CONCAT(numero, '/', numloja)                       AS doc,
       ''                                                 AS nfEnt,
       IF(noRota = 0, 'REPOSICAO_CDLJ', 'REPOSICAO_LJCD') AS tipo,
       IF(noRota = 1,
          CONCAT('da\tLoja\t', numero),
          'para\to\tCD')                                  AS observacao,
       NULL                                               AS vencimento,
       IF(noRota = 1, movimentacao, -movimentacao)        AS qtde,
       0                                                  AS saldo,
       IF(noRota = 1, ER.sname, EE.sname)                 AS userLogin
FROM
  T_MOVIMENTACAO_ESTOQUE   AS E
    LEFT JOIN sqldados.emp AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.emp AS EE
              ON EE.no = E.noEntregue
WHERE noRota IN (0, 1);

INSERT INTO T_MOVIMENTACAO_KARDEC(loja, prdno, grade, data, doc, nfEnt, tipo, observacao, vencimento, qtde, saldo,
                                  userLogin)
SELECT CASE noRota
         WHEN 42 THEN 2
         WHEN 43 THEN 3
         WHEN 45 THEN 4
         WHEN 48 THEN 5
       END                                         AS loja,
       prdno,
       grade,
       data,
       CONCAT(numero, '/', numloja)                AS doc,
       ''                                          AS nfEnt,
       CASE noRota
         WHEN 42 THEN 'REPOSICAO_CDLJ2'
         WHEN 43 THEN 'REPOSICAO_CDLJ3'
         WHEN 45 THEN 'REPOSICAO_CDLJ5'
         WHEN 48 THEN 'REPOSICAO_CDLJ8'
       END                                         AS tipo,
       IF(noRota = 1,
          CONCAT('da\tLoja\t', numero),
          'para\to\tCD')                           AS observacao,
       NULL                                        AS vencimento,
       IF(noRota = 1, movimentacao, -movimentacao) AS qtde,
       0                                           AS saldo,
       IF(noRota = 1, ER.sname, EE.sname)          AS userLogin
FROM
  T_MOVIMENTACAO_ESTOQUE   AS E
    LEFT JOIN sqldados.emp AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.emp AS EE
              ON EE.no = E.noEntregue
WHERE noRota IN (42, 43, 45, 48);

SELECT loja,
       prdno,
       grade,
       data,
       doc,
       nfEnt,
       tipo,
       '' AS observacao,
       vencimento,
       qtde,
       saldo,
       userLogin
FROM
  T_MOVIMENTACAO_KARDEC

