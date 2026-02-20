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
  AND data >= :dataIncial
  AND noGravado > 0
  AND noRota IN (0, 1);

/*
select * from sqldados.produtoKardec
*/

/*Loado da loja*/
DROP TEMPORARY TABLE IF EXISTS T_MOVIMENTACAO_KARDEC;
CREATE TEMPORARY TABLE T_MOVIMENTACAO_KARDEC
SELECT numloja                                          AS loja,
       prdno,
       grade,
       data,
       numero                                           AS doc,
       ''                                               AS nfEnt,
       IF(noRota = 0, 'MOV_RECEBIMENTO', 'MOV_ENTREGA') AS tipo,
       ''                                               AS observacao,
       NULL                                             AS vencimento,
       IF(noRota = 0, movimentacao, -movimentacao)      AS qtde,
       0                                                AS saldo,
       IF(noRota = 0, ER.sname, EE.sname)               AS userLogin
FROM
  T_MOVIMENTACAO_ESTOQUE   AS E
    LEFT JOIN sqldados.emp AS ER
              ON ER.no = E.noRecebido
    LEFT JOIN sqldados.emp AS EE
              ON EE.no = E.noEntregue
WHERE numloja = :loja;

INSERT INTO T_MOVIMENTACAO_KARDEC(loja, prdno, grade, data, doc, nfEnt, tipo, observacao, vencimento, qtde, saldo,
                                  userLogin)
SELECT 4                                                AS loja,
       prdno,
       grade,
       data,
       numero                                           AS doc,
       ''                                               AS nfEnt,
       IF(noRota = 1, 'MOV_RECEBIMENTO', 'MOV_ENTREGA') AS tipo,
       ''                                               AS observacao,
       NULL                                             AS vencimento,
       IF(noRota = 1, movimentacao, -movimentacao)      AS qtde,
       0                                                AS saldo,
       IF(noRota = 1, ER.sname, EE.sname)               AS userLogin
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
       observacao,
       vencimento,
       qtde,
       saldo,
       userLogin
FROM
  T_MOVIMENTACAO_KARDEC
