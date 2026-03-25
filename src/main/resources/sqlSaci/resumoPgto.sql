USE sqldados;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, NULL);

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  INDEX (loja, pdv, transacao)
)
SELECT N.storeno                                                AS loja,
       N.pdvno                                                  AS pdv,
       N.xano                                                   AS transacao,
       N.paymno                                                 AS numMetodo,
       M.sname                                                  AS nomeMetodo,
       M.mult / 10000                                           AS mult,
       N.eordno                                                 AS pedido,
       CAST(N.issuedate AS DATE)                                AS data,
       N.nfno                                                   AS nfno,
       N.nfse                                                   AS nfse,
       CONCAT(N.nfno, '/', N.nfse)                              AS nota,
       CASE
         WHEN N.tipo = 0  THEN 'VENDA NF'
         WHEN N.tipo = 1  THEN 'TRANSFERENCIA'
         WHEN N.tipo = 2  THEN 'DEVOLUCAO'
         WHEN N.tipo = 3  THEN 'SIMP REME'
         WHEN N.tipo = 4  THEN 'ENTRE FUT'
         WHEN N.tipo = 5  THEN 'RET DEMON'
         WHEN N.tipo = 6  THEN 'VENDA USA'
         WHEN N.tipo = 7  THEN 'OUTROS'
         WHEN N.tipo = 8  THEN 'NF CF'
         WHEN N.tipo = 9  THEN 'PERD/CONSER'
         WHEN N.tipo = 10 THEN 'REPOSICAO'
         WHEN N.tipo = 11 THEN 'RESSARCI'
         WHEN N.tipo = 12 THEN 'COMODATO'
         WHEN N.tipo = 13 THEN 'NF EMPRESA'
         WHEN N.tipo = 14 THEN 'BONIFICA'
         WHEN N.tipo = 15 THEN 'NFE'
                          ELSE 'TIPO INVALIDO'
       END                                                      AS tipoNf,
       SEC_TO_TIME(P.time)                                      AS hora,
       Q.string                                                 AS tipoPgto,
       N.grossamt / 100                                         AS valor,
       N.custno                                                 AS cliente,
       C.name                                                   AS nomeCliente,
       IF(C.cpf_cgc LIKE 'NAO%', '', IFNULL(A.state, C.state1)) AS uf,
       CONCAT(E.no, ' - ', MID(E.sname, 1, 17))                 AS vendedor,
       IFNULL(SUM(V.amt / 100), N.grossamt / 100)               AS valorTipo,
       CONCAT(N.remarks, ' ', N.print_remarks)                  AS obs
FROM
  sqldados.nf                  AS N
    LEFT JOIN  sqldados.paym   AS M
               ON N.paymno = M.no
    LEFT JOIN  sqldados.ctadd  AS A
               ON A.custno = N.custno AND A.seqno = N.custno_addno
    LEFT JOIN  sqlpdv.pxa      AS P
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqlpdv.pxaval   AS V
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.custp  AS C
               ON C.no = N.custno
    INNER JOIN sqldados.emp    AS E
               ON E.no = N.empno
    LEFT JOIN  sqldados.query1 AS Q
               ON Q.no_short = IF(N.xatype = 999, V.xatype, N.xatype)
WHERE (N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.tipo IN (0, 4)
  AND N.status <> 1
GROUP BY N.storeno, N.pdvno, N.xano, IF(N.xatype = 999, V.xatype, N.xatype)
HAVING (@PESQUISA = '' OR pedido = @PESQUISA_INT OR pdv = @PESQUISA_INT OR nota LIKE @PESQUISA_START OR
        tipoNf LIKE @PESQUISA_LIKE OR tipoPgto LIKE @PESQUISA_LIKE OR cliente LIKE @PESQUISA_INT OR
        UPPER(obs) REGEXP CONCAT('NI[^0-9A-Z]*', @PESQUISA_INT) OR nomeCliente LIKE @PESQUISA_LIKE OR
        vendedor LIKE @PESQUISA_LIKE OR transacao = @PESQUISA_INT)
ORDER BY N.storeno, N.pdvno, N.xano, tipoNf, tipoPgto;

DROP TEMPORARY TABLE IF EXISTS T_CHAVE;
CREATE TEMPORARY TABLE T_CHAVE
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja, pdv, transacao, nfno, nfse, data AS dataVenda
FROM
  T_NOTA
GROUP BY loja, pdv, transacao;

DROP TEMPORARY TABLE IF EXISTS T_CARD;
CREATE TEMPORARY TABLE T_CARD
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja,
       pdv,
       transacao,
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
GROUP BY loja, pdv, transacao;


DROP TEMPORARY TABLE IF EXISTS T_DUP;
CREATE TEMPORARY TABLE T_DUP
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT C.loja,
       C.pdv,
       C.transacao,
       COUNT(DISTINCT D.dupse)                                                    AS quantParcelas,
       TRUNCATE((SUM(DATEDIFF(duedate, dataVenda)) / COUNT(DISTINCT D.dupse)), 0) AS mediaPrazo
FROM
  T_CHAVE                     AS C
    INNER JOIN sqldados.nfdup AS N
               ON N.nfstoreno = C.loja
                 AND N.nfno = C.nfno
                 AND N.nfse = C.nfse
    INNER JOIN sqldados.dup   AS D
               ON D.storeno = N.dupstoreno
                 AND D.type = N.duptype
                 AND D.dupno = N.dupno
                 AND D.dupse = N.dupse
GROUP BY loja, pdv, transacao;

SELECT loja,
       pdv,
       transacao,
       numMetodo,
       nomeMetodo,
       mult,
       pedido,
       data,
       nota,
       tipoNf,
       documento,
       -- COALESCE(C.quantParcelas, D.quantParcelas, 0) AS quantParcelas,
       CASE
         WHEN tipoPgto LIKE 'DUP%'  THEN D.quantParcelas
         WHEN tipoPgto LIKE 'CART%' THEN C.quantParcelas
                                    ELSE 0
       END AS quantParcelas,
       -- COALESCE(C.mediaPrazo, D.mediaPrazo, 0)       AS mediaPrazo,
       CASE
         WHEN tipoPgto LIKE 'DUP%'  THEN D.mediaPrazo
         WHEN tipoPgto LIKE 'CART%' THEN C.mediaPrazo
                                    ELSE 0
       END AS mediaPrazo,
       hora,
       tipoPgto,
       valor,
       cliente,
       nomeCliente,
       uf,
       vendedor,
       valorTipo,
       obs
FROM
  T_NOTA             AS N
    LEFT JOIN T_CARD AS C
              USING (loja, pdv, transacao)
    LEFT JOIN T_DUP  AS D
              USING (loja, pdv, transacao)

