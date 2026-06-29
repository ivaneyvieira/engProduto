USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_CHAVE;
CREATE TEMPORARY TABLE T_CHAVE
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT storeno AS loja, pdvno AS pdv, xano AS transacao, nfno, nfse, CAST(issuedate AS date) AS dataVenda
FROM sqldados.nf
WHERE storeno = :loja
  AND pdvno = :pdv
  AND xano = :transacao
GROUP BY loja, pdv, transacao;


DROP TEMPORARY TABLE IF EXISTS T_CARD;
CREATE TEMPORARY TABLE T_CARD
(
  PRIMARY KEY (loja, pdv, transacao, seqno)
)
SELECT CR.storeno                     AS loja,
       CR.pdvno                       AS pdv,
       CR.xano                        AS transacao,
       dataVenda                      AS dataVenda,
       CR.seqno                       AS seqno,
       'Cart]ao/Pix'                  AS tipo,
       CAST(MAX(CR.recvdate) AS date) AS dataParcela,
       SUM(CR.amt / 100)              AS valorParcela,
       CT.sname                       AS documento
FROM
  T_CHAVE                    AS C
    INNER JOIN sqlpdv.pxacrd AS CR
               ON C.loja = CR.storeno AND C.pdv = CR.pdvno AND C.transacao = CR.xano
    LEFT JOIN  sqldados.card AS CT
               ON CT.no = CR.cardno
GROUP BY loja, pdv, transacao, seqno;


DROP TEMPORARY TABLE IF EXISTS T_DUP;
CREATE TEMPORARY TABLE T_DUP
(
  PRIMARY KEY (loja, pdv, transacao, seqno)
)
SELECT storeno                      AS loja,
       pdvno                        AS pdv,
       xano                         AS transacao,
       dataVenda                    AS dataVenda,
       D.dupno                      AS seqno,
       'Duplicata'                  AS tipo,
       CAST(MAX(D.duedate) AS date) AS dataParcela,
       SUM(D.amtdue / 100)          AS valorParcela
FROM
  T_CHAVE                     AS C
    INNER JOIN sqldados.nfdup AS N
               ON N.nfstoreno = C.loja AND N.nfno = C.nfno AND N.nfse = C.nfse
    INNER JOIN sqldados.dup   AS D
               ON D.storeno = N.dupstoreno AND D.type = N.duptype AND D.dupno = N.dupno AND D.dupse = N.dupse
GROUP BY loja, pdv, transacao, D.dupno;

SELECT loja, pdv, transacao, seqno, tipo, dataVenda, dataParcela, valorParcela, documento
FROM T_CARD
UNION
SELECT loja, pdv, transacao, seqno, tipo, dataVenda, dataParcela, valorParcela, '' AS documento
FROM T_DUP