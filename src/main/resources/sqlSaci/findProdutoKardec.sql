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
        WHEN 46   THEN (P.qtty % 900) != 0
        WHEN 1040 THEN (P.qtty % 1000) != 0
                  ELSE TRUE
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

SELECT loja, prdno, grade, data, doc, tipo, qtde, observacao, saldo
FROM
  T_KARDEX
ORDER BY data, loja, prdno, grade, doc
