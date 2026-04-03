USE sqldados;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, NULL);

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  INDEX (loja, pdv, transacao, seqno)
)
SELECT N.storeno                                  AS loja,
       N.pdvno                                    AS pdv,
       N.xano                                     AS transacao,
       M.mult / 10000                             AS mult,
       CAST(N.issuedate AS DATE)                  AS data,
       Q.string                                   AS tipoPgto,
       N.grossamt / 100                           AS valor,
       IFNULL(SUM(V.amt / 100), N.grossamt / 100) AS valorTipo,
       IFNULL(V.seqno, 0)                         AS seqno
FROM
  sqldados.nf                 AS N
    LEFT JOIN sqldados.paym   AS M
              ON N.paymno = M.no
    LEFT JOIN sqlpdv.pxa      AS P
              USING (storeno, pdvno, xano)
    LEFT JOIN sqlpdv.pxaval   AS V
              USING (storeno, pdvno, xano)
    LEFT JOIN sqldados.query1 AS Q
              ON Q.no_short = IF(N.xatype = 999, V.xatype, N.xatype)
WHERE (N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.tipo IN (0, 4)
  AND N.status <> 1
  AND Q.string LIKE 'Cartao%Cred%'
GROUP BY N.storeno, N.pdvno, N.xano, IFNULL(V.seqno, 0), tipoPgto
HAVING (@PESQUISA = '' OR pdv = @PESQUISA_INT OR
        tipoPgto LIKE @PESQUISA_LIKE OR
        transacao = @PESQUISA_INT);

DROP TEMPORARY TABLE IF EXISTS T_CHAVE;
CREATE TEMPORARY TABLE T_CHAVE
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja, pdv, transacao, data AS dataVenda
FROM
  T_NOTA
GROUP BY loja, pdv, transacao;

DROP TEMPORARY TABLE IF EXISTS T_CARD;
CREATE TEMPORARY TABLE T_CARD
(
  PRIMARY KEY (loja, pdv, transacao, seqno)
)
SELECT loja,
       pdv,
       transacao,
       CR.s2                                                                        AS seqno,
       CR.cardno                                                                    AS cardno,
       COUNT(DISTINCT CR.seqno)                                                     AS quantParcelas,
       TRUNCATE((SUM(DATEDIFF(recvdate, dataVenda)) / COUNT(DISTINCT CR.seqno)), 0) AS mediaPrazo,
       CT.sname                                                                     AS documento
FROM
  T_CHAVE                    AS C
    INNER JOIN sqlpdv.pxacrd AS CR
               ON C.loja = CR.storeno
                 AND C.pdv = CR.pdvno
                 AND C.transacao = CR.xano
    LEFT JOIN  sqldados.card AS CT
               ON CT.no = CR.cardno
GROUP BY loja, pdv, transacao, CR.s2;

SELECT loja,
       pdv,
       transacao,
       mult,
       data,
       documento,
       C.quantParcelas                               AS quantParcelas,
       C.mediaPrazo                                  AS mediaPrazo,
       tipoPgto,
       valor,
       valorTipo,
       valorTipo - (valorTipo / IFNULL(mult, 1.000)) AS valorFin
FROM
  T_NOTA             AS N
    LEFT JOIN T_CARD AS C
              USING (loja, pdv, transacao, seqno)
WHERE tipoPgto LIKE 'Cartao%Cred%'
