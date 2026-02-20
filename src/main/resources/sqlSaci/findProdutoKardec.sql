USE sqldados;

SET SQL_MODE = '';

DO @DATA_FINAL := ROUND(CURDATE() * 1);

DROP TABLE IF EXISTS T_KARDEX;
CREATE TEMPORARY TABLE T_KARDEX
(
  tipo       VARCHAR(15),
  observacao VARCHAR(255)
)
SELECT storeno                      AS loja,
       prdno                        AS prdno,
       grade                        AS grade,
       CAST(date AS date)           AS data,
       SUBSTRING_INDEX(doc, '.', 1) AS doc,
       'VENDA'                      AS tipo,
       ROUND(-qtty / 1000)          AS qtde,
       N.remarks                    AS observacao,
       0                            AS saldo
FROM
  sqldados.xalog2          AS X
    INNER JOIN sqldados.nf AS N
               USING (storeno, pdvno, xano)
WHERE prdno = :prdno
  AND grade = :grade
  AND storeno = :loja
  AND date BETWEEN :dataInicial AND @DATA_FINAL
  AND qtty > 0
  AND N.tipo = 0
  AND N.status <> 1;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo)
SELECT N.storeno                   AS loja,
       P.prdno                     AS prdno,
       P.grade                     AS grade,
       CAST(N.issuedate AS date)   AS data,
       CONCAT(N.nfno, '/', N.nfse) AS doc,
       'FATURA'                    AS tipo,
       ROUND(-P.qtty)              AS qtde,
       remarks                     AS observacao,
       0                           AS saldo
FROM
  sqldados.nf                 AS N
    INNER JOIN sqldados.xaprd AS P
               USING (storeno, pdvno, xano)
WHERE P.prdno = :prdno
  AND P.grade = :grade
  AND N.storeno = :loja
  AND N.tipo = 3
  AND N.nfse = '3'
  AND N.issuedate BETWEEN :dataInicial AND @DATA_FINAL
  AND N.status <> 1;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo)
SELECT N.storeno                   AS loja,
       P.prdno                     AS prdno,
       P.grade                     AS grade,
       CAST(N.issuedate AS date)   AS data,
       CONCAT(N.nfno, '/', N.nfse) AS doc,
       'TRANSF'                    AS tipo,
       ROUND(-P.qtty)              AS qtde,
       remarks                     AS observacao,
       0                           AS saldo
FROM
  sqldados.nf                 AS N
    INNER JOIN sqldados.xaprd AS P
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.prd   AS C
               ON C.no = P.prdno
WHERE P.prdno = :prdno
  AND P.grade = :grade
  AND N.storeno = :loja
  AND N.tipo = 1
  AND N.issuedate BETWEEN :dataInicial AND @DATA_FINAL
  AND N.status <> 1
  AND CASE C.mfno
        WHEN 46   THEN (ROUND(P.qtty) % 900) != 0
        WHEN 1040 THEN (ROUND(P.qtty) % 1000) != 0
                  ELSE remarks NOT REGEXP '^RES[A-Z]+ *PED +[0-9]{9} '
      END;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo)
SELECT N.storeno                      AS loja,
       P.prdno                        AS prdno,
       P.grade                        AS grade,
       CAST(N.comp_date AS date)      AS data,
       CONCAT(N.nfname, '/', N.invse) AS doc,
       'DEVOLUCAO'                    AS tipo,
       ROUND(P.qtty / 1000)           AS qtde,
       remarks                        AS observacao,
       0                              AS saldo
FROM
  sqldados.inv               AS N
    INNER JOIN sqldados.iprd AS P
               USING (invno)
WHERE P.prdno = :prdno
  AND P.grade = :grade
  AND N.storeno = :loja
  AND N.type = 2
  AND N.bits & POW(2, 4) = 0
  AND N.invno NOT IN ( SELECT nfNfno FROM sqldados.inv WHERE auxShort13 & POW(2, 15) != 0 )
  AND N.comp_date BETWEEN :dataInicial AND @DATA_FINAL;

DROP TABLE IF EXISTS T_REPOSICAO;
CREATE TEMPORARY TABLE T_REPOSICAO
SELECT O.storeno                                    AS loja,
       E.prdno                                      AS prdno,
       E.grade                                      AS grade,
       CAST(O.date AS DATE)                         AS data,
       O.ordno                                      AS doc,
       ROUND(E.qtty / 1000)                         AS qtde,
       TRIM(MID(IFNULL(R.remarks__480, ''), 1, 40)) AS observacao,
       CASE O.paymno
         WHEN 431 THEN 'REPOSICAO'
         WHEN 432 THEN 'RETORNO'
         WHEN 433 THEN 'ACERTO'
                  ELSE ''
       END                                          AS metodo,
       IFNULL(EA.marca, 0)                          AS marca,
       IF(O.paymno = 433,
          ROUND(CASE
                  WHEN R.remarks__480 LIKE 'ENTRADA%' THEN 1
                  WHEN R.remarks__480 LIKE 'SAIDA%'   THEN -1
                                                      ELSE 0
                END), 1)                            AS multAcerto
FROM
  sqldados.eoprd                       AS E
    LEFT JOIN  sqldados.eoprdAdicional AS EA
               USING (storeno, ordno, prdno, grade)
    INNER JOIN sqldados.eord           AS O
               USING (storeno, ordno)
    LEFT JOIN  sqldados.eordrk         AS R
               USING (storeno, ordno)
WHERE (O.paymno IN (431, 432, 433))
  AND (O.date BETWEEN :dataInicial AND @DATA_FINAL)
  AND (E.storeno = :loja)
  AND (E.prdno = :prdno)
  AND (E.grade = :grade)
GROUP BY E.storeno, E.ordno, E.prdno, E.grade
HAVING multAcerto != 0;

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo)
SELECT loja, prdno, grade, data, doc, metodo AS tipo, qtde * multAcerto AS qtde, observacao, 0 AS saldo
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
SELECT numloja                                          AS loja,
       prdno,
       grade,
       data,
       numero                                           AS doc,
       ''                                               AS nfEnt,
       IF(noRota = 0, 'MOV_RECEBIMENTO', 'MOV_ENTREGA') AS tipo,
       IF(noRota = 0,
          'do\tCD',
          CONCAT('para\ta\tLoja\t', numero))            AS observacao,
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
       IF(noRota = 1,
          CONCAT('da\tLoja\t', numero),
          'para\to\tCD')                                AS observacao,
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

INSERT INTO T_KARDEX(loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo)
SELECT loja, prdno, grade, data, doc, tipo, qtde, observacao, 0 AS saldo
FROM
  T_MOVIMENTACAO_KARDEC
WHERE tipo = 'MOV_RECEBIMENTO';

SELECT loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo
FROM
  T_KARDEX
ORDER BY data, loja, prdno, grade, doc
