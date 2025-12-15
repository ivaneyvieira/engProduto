USE sqldados;

DO @DATA_FINAL := ROUND(CURDATE() * 1);

DROP TABLE IF EXISTS T_VENDA;
CREATE TEMPORARY TABLE T_VENDA(
  tipo VARCHAR(15)
)
SELECT storeno                      AS loja,
       prdno                        AS prdno,
       grade                        AS grade,
       CAST(date AS date)           AS data,
       SUBSTRING_INDEX(doc, '.', 1) AS doc,
       'VENDA'                      AS tipo,
       ROUND(-qtty / 1000)          AS qtde,
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
  AND N.tipo = 0;

INSERT INTO T_VENDA(loja, prdno, grade, data, doc, tipo, qtde, saldo)
SELECT N.storeno                   AS loja,
       P.prdno                     AS prdno,
       P.grade                     AS grade,
       CAST(N.issuedate AS date)   AS data,
       CONCAT(N.nfno, '/', N.nfse) AS doc,
       'FATURA'                    AS tipo,
       ROUND(-P.qtty)              AS qtde,
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
  AND N.issuedate BETWEEN :dataInicial AND @DATA_FINAL;

INSERT INTO T_VENDA(loja, prdno, grade, data, doc, tipo, qtde, saldo)
SELECT N.storeno                   AS loja,
       P.prdno                     AS prdno,
       P.grade                     AS grade,
       CAST(N.issuedate AS date)   AS data,
       CONCAT(N.nfno, '/', N.nfse) AS doc,
       'TRANSF'                    AS tipo,
       ROUND(-P.qtty)              AS qtde,
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
  AND CASE C.mfno
        WHEN 46   THEN (P.qtty % 900) != 0
        WHEN 1040 THEN (P.qtty % 1000) != 0
                  ELSE TRUE
      END;

SELECT loja, prdno, grade, data, doc, tipo, qtde, saldo
FROM
  T_VENDA
ORDER BY data, loja, prdno, grade, doc
