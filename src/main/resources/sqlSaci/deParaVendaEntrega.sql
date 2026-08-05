USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_V;
CREATE TEMPORARY TABLE T_V
(
  INDEX (storeno, ordno)
)
SELECT P.storeno,
       P.pdvno,
       P.xano,
       P.eordno                                  AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       nfno,
       nfse
FROM sqlpdv.pxa AS P
WHERE P.cfo IN (5922, 6922)
  AND storeno IN (2, 3, 4, 5, 8)
  AND nfse = '1'
  AND date >= :dataCorte;

DROP TEMPORARY TABLE IF EXISTS T_E;
CREATE TEMPORARY TABLE T_E
(
  INDEX (storeno, ordno)
)
SELECT P.storeno,
       P.pdvno,
       P.xano,
       P.eordno                                  AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       P.date                                    AS data
FROM
  sqlpdv.pxa       AS P
    INNER JOIN T_V AS V
               ON P.storeno = V.storeno AND P.eordno = V.ordno
WHERE P.cfo IN (5117, 6117)
  AND P.storeno IN (2, 3, 4, 5, 8);

DROP TEMPORARY TABLE IF EXISTS T_ENTREGA;
CREATE TEMPORARY TABLE T_ENTREGA
(
  INDEX (transacao),
  INDEX (transacaoE)
)
SELECT V.storeno AS loja,
       V.pdvno   AS pdv,
       V.xano    AS transacao,
       V.numero  AS notaVenda,
       E.storeno AS lojaE,
       E.pdvno   AS pdvE,
       E.xano    AS transacaoE,
       E.numero  AS notaEntrega
FROM
  T_V              AS V
    INNER JOIN T_E AS E
               USING (storeno, ordno);

SELECT loja, pdv, transacao, notaVenda, lojaE, pdvE, transacaoE, notaEntrega
FROM T_ENTREGA
WHERE transacao = :xano
   OR transacaoE = :xano