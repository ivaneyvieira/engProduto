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
  AND noRota IN (0, 1);

/*Loado da loja*/
DROP TEMPORARY TABLE IF EXISTS T_MOVIMENTACAO_KARDEC;
CREATE TEMPORARY TABLE T_MOVIMENTACAO_KARDEC
SELECT numloja                                            AS loja,
       prdno,
       grade,
       data,
       CONCAT(numero, '/', numloja)                       AS doc,
       ''                                                 AS nfEnt,
       IF(noRota = 0, 'REPOSICAO_CDLJ', 'REPOSICAO_LJCD') AS tipo,
       IF(noRota = 0,
          'do\tCD',
          CONCAT('para\ta\tLoja\t', numero))              AS observacao,
       NULL                                               AS vencimento,
       IF(noRota = 0, movimentacao, -movimentacao)        AS qtde,
       0                                                  AS saldo,
       IF(noRota = 0, ER.login, EE.login)                 AS userLogin
FROM
  T_MOVIMENTACAO_ESTOQUE   AS E
    LEFT JOIN sqldados.users AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.users AS EE
              ON EE.no = E.noEntregue
WHERE numloja = :loja;

INSERT INTO T_MOVIMENTACAO_KARDEC(loja, prdno, grade, data, doc, nfEnt, tipo, observacao, vencimento, qtde, saldo,
                                  userLogin)
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
              ON EE.no = E.noEntregue;

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
WHERE qtde < 0
