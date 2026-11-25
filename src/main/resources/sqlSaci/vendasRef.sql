USE sqldados;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_REGEXP := CONCAT('.*', REPLACE(@PESQUISA, ' ', ' +'), '.*');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, NULL);

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  INDEX (loja, pdv, transacao)
)
SELECT N.storeno                                                AS loja,
       S.sname                                                  AS siglaLoja,
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
       N.fre_amt / 100                                          AS frete,
       N.custno                                                 AS cliente,
       C.name                                                   AS nomeCliente,
       IF(C.cpf_cgc LIKE 'NAO%', '', IFNULL(A.state, C.state1)) AS uf,
       CONCAT(E.no, ' - ', MID(E.sname, 1, 17))                 AS vendedor,
       IFNULL(SUM(V.amt / 100), N.grossamt / 100)               AS valorTipo,
       CONCAT(N.remarks, ' ', N.print_remarks)                  AS obs,
       CAST(RPAD(AD.addr, 60, ' ') AS CHAR)                     AS enderecoEntrega,
       RPAD(AD.nei, 25, ' ')                                    AS bairroEntrega,
       IFNULL(IF(AD.city = 'TIMON', 'TIMON', AR.name), '')      AS area,
       IFNULL(RT.name, '')                                      AS rota,
       C.add1                                                   AS endereco,
       C.nei1                                                   AS bairro,
       V.impresso                                               AS impresso,
       V.dataImpressao                                          AS dataImpressao,
       V.horaImpessao                                           AS horaImpessao
FROM
  sqldados.nf                     AS N
    LEFT JOIN  sqldados.vendasRef AS V
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.store     AS S
               ON S.no = N.storeno
    LEFT JOIN  sqldados.paym      AS M
               ON N.paymno = M.no
    LEFT JOIN  sqldados.ctadd     AS A
               ON A.custno = N.custno AND A.seqno = N.custno_addno
    LEFT JOIN  sqlpdv.pxa         AS P
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqlpdv.pxaval      AS V
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.custp     AS C
               ON C.no = N.custno
    INNER JOIN sqldados.emp       AS E
               ON E.no = N.empno
    LEFT JOIN  sqldados.query1    AS Q
               ON Q.no_short = IF(N.xatype = 999, V.xatype, N.xatype)
    LEFT JOIN  sqldados.ctadd     AS AD
               ON AD.seqno = N.custno_addno AND AD.custno = N.custno
    LEFT JOIN  sqldados.route     AS RT
               ON (AD.routeno = RT.no)
    LEFT JOIN  sqldados.area      AS AR
               ON (AR.no = RT.areano)
WHERE (N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.pdvno = :pdv OR :pdv = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.tipo IN (0, 4)
  AND N.status <> 1
GROUP BY N.storeno, N.pdvno, N.xano, IF(N.xatype = 999, V.xatype, N.xatype)
HAVING (@PESQUISA = '' OR pedido = @PESQUISA_INT OR pdv = @PESQUISA_INT OR nota LIKE @PESQUISA_START OR
        tipoNf LIKE @PESQUISA_LIKE OR tipoPgto LIKE @PESQUISA_LIKE OR cliente LIKE @PESQUISA_INT OR
        UPPER(obs) REGEXP CONCAT('NI[^0-9A-Z]*', @PESQUISA_INT) OR nomeCliente LIKE @PESQUISA_LIKE OR
        vendedor LIKE @PESQUISA_LIKE OR transacao = @PESQUISA_INT OR M.sname REGEXP @PESQUISA_REGEXP)
   AND (tipoNf = :tipoNF OR :tipoNF = '');

DROP TEMPORARY TABLE IF EXISTS T_CHAVE;
CREATE TEMPORARY TABLE T_CHAVE
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja, pdv, transacao, nfno, nfse, data AS dataVenda
FROM T_NOTA
GROUP BY loja, pdv, transacao;

DROP TEMPORARY TABLE IF EXISTS T_MOTORISTA;
CREATE TEMPORARY TABLE T_MOTORISTA
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja,
       pdv,
       transacao,
       COUNT(*)                                                                                  AS ctMotorista,
       MID(MAX(CONCAT(LPAD(IF(A.auxShort4 = 0, 0, A.date), 10, '0'), A.auxShort4)), 11, 100) * 1 AS noMotorista
FROM
  sqldados.awnfrh      AS A
    INNER JOIN T_CHAVE AS C
               ON C.loja = A.storenoNfr AND C.pdv = A.pdvnoNfr AND C.transacao = A.xanoNfr
GROUP BY loja, pdv, transacao;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_FATURA;
CREATE TEMPORARY TABLE T_PEDIDO_FATURA
(
  PRIMARY KEY (storeno, eordno)
)
SELECT storeno, eordno, MIN(DATE) AS date
FROM
  sqlpdv.pxa           AS P
    INNER JOIN T_CHAVE AS C
               ON P.storeno = C.loja AND P.pdvno = C.pdv AND P.xano = C.transacao
GROUP BY storeno, eordno;

DO @DATAPED := ( SELECT MIN(date)
                 FROM T_PEDIDO_FATURA );

DROP TEMPORARY TABLE IF EXISTS T_PXA;
CREATE TEMPORARY TABLE T_PXA
(
  INDEX (storeno, eordno)
)
SELECT storeno, eordno, pdvno, xano, nfno, nfse, date, cfo, time, amt
FROM sqlpdv.pxa AS P
WHERE (P.storeno IN (2, 3, 4, 5, 8))
  AND (P.storeno = :loja OR :loja = 0)
  AND P.cfo IN (5922, 6922, 5117, 6117)
  AND P.date >= @DATAPED;

DROP TEMPORARY TABLE IF EXISTS T_DEP_ENTREGA;
CREATE TEMPORARY TABLE T_DEP_ENTREGA
(
  PRIMARY KEY (storeno, ordno),
  INDEX (storeno_entrega, pdvno_entrega, xano_entrega),
  INDEX (storeno_VENDA, pdvno_venda, xano_venda)
)
SELECT P.storeno,
       P.eordno                                        AS ordno,
       P.time,
       MAX(IF(P.cfo IN (5922, 6922), P.storeno, NULL)) AS storeno_venda,
       MAX(IF(P.cfo IN (5922, 6922), P.pdvno, NULL))   AS pdvno_venda,
       MAX(IF(P.cfo IN (5922, 6922), P.xano, NULL))    AS xano_venda,
       MAX(IF(P.cfo IN (5922, 6922), P.date, NULL))    AS data_venda,
       MAX(IF(P.cfo IN (5922, 6922), P.nfno, NULL))    AS nfno_venda,
       MAX(IF(P.cfo IN (5922, 6922), P.nfse, NULL))    AS nfse_venda,
       MAX(IF(P.cfo IN (5922, 6922), P.amt, NULL))     AS valor_venda,
       MAX(IF(P.cfo IN (5117, 6117), P.storeno, NULL)) AS storeno_entrega,
       MAX(IF(P.cfo IN (5117, 6117), P.pdvno, NULL))   AS pdvno_entrega,
       MAX(IF(P.cfo IN (5117, 6117), P.xano, NULL))    AS xano_entrega,
       MAX(IF(P.cfo IN (5117, 6117), P.date, NULL))    AS data_entrega,
       MAX(IF(P.cfo IN (5117, 6117), P.nfno, NULL))    AS nfno_entrega,
       MAX(IF(P.cfo IN (5117, 6117), P.nfse, NULL))    AS nfse_entrega,
       MAX(IF(P.cfo IN (5117, 6117), P.amt, NULL))     AS valor_entrega
FROM
  T_PEDIDO_FATURA    AS F
    INNER JOIN T_PXA AS P
               USING (storeno, eordno)
GROUP BY P.storeno, P.eordno;

DROP TEMPORARY TABLE IF EXISTS T_TIPO;
CREATE TEMPORARY TABLE T_TIPO
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja, pdv, transacao, IF(E.bits & POW(2, 1) != 0, 'R', 'E') AS tipo
FROM
  sqlpdv.pxa                   AS P
    INNER JOIN T_CHAVE         AS C
               ON C.loja = P.storeno AND C.pdv = P.pdvno AND C.transacao = P.xano
    LEFT JOIN  sqldados.eoprdf AS E
               ON E.storeno = P.storeno AND P.eordno = E.ordno AND E.date >= @DATAPED
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
               ON C.loja = CR.storeno AND C.pdv = CR.pdvno AND C.transacao = CR.xano
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
               ON N.nfstoreno = C.loja AND N.nfno = C.nfno AND N.nfse = C.nfse
    INNER JOIN sqldados.dup   AS D
               ON D.storeno = N.dupstoreno AND D.type = N.duptype AND D.dupno = N.dupno AND D.dupse = N.dupse
GROUP BY loja, pdv, transacao;

SELECT loja,
       siglaLoja,
       pdv,
       transacao,
       numMetodo,
       nomeMetodo,
       mult,
       pedido,
       data,
       nota,
       tipoNf,
       T.tipo,
       documento,
       -- COALESCE(C.quantParcelas, D.quantParcelas, 0) AS quantParcelas,
       CASE
         WHEN tipoPgto LIKE 'DUP%'  THEN D.quantParcelas
         WHEN tipoPgto LIKE 'CART%' THEN C.quantParcelas
                                    ELSE 0
       END                                                                     AS quantParcelas,
       -- COALESCE(C.mediaPrazo, D.mediaPrazo, 0)       AS mediaPrazo,
       CASE
         WHEN tipoPgto LIKE 'DUP%'  THEN D.mediaPrazo
         WHEN tipoPgto LIKE 'CART%' THEN C.mediaPrazo
                                    ELSE 0
       END                                                                     AS mediaPrazo,
       hora,
       tipoPgto,
       valor,
       valor + frete                                                           AS valorComFrete,
       cliente,
       nomeCliente,
       uf,
       vendedor,
       valorTipo,
       obs,
       enderecoEntrega,
       bairroEntrega,
       area,
       rota,
       RPAD(IFNULL(MID(obs, 1, 80), ' '), 80, ' ')                             AS obs1,
       RPAD(IFNULL(MID(obs, 81, 80), ' '), 80, ' ')                            AS obs2,
       RPAD(IFNULL(MID(obs, 161, 80), ' '), 80, ' ')                           AS obs3,
       RPAD(IFNULL(MID(obs, 241, 80), ' '), 80, ' ')                           AS obs4,
       RPAD(IFNULL(MID(obs, 321, 80), ' '), 80, ' ')                           AS obs5,
       RPAD(IFNULL(MID(obs, 401, 80), ' '), 80, ' ')                           AS obs6,
       RPAD(IFNULL(MID(obs, 481, 80), ' '), 80, ' ')                           AS obs7,
       endereco                                                                AS endereco,
       bairro                                                                  AS bairro,
       CONCAT(F.nfno_entrega, '/', F.nfse_entrega)                             AS nfEntrega,
       CAST(IF(F.data_entrega = 0, NULL, F.data_entrega) AS date)              AS dataEntrega,
       IFNULL(N.impresso, 0)                                                   AS impresso,
       CAST(IF(IFNULL(N.dataImpressao, 0) = 0, NULL, N.dataImpressao) AS date) AS dataImpressao,
       SEC_TO_TIME(N.horaImpessao)                                             AS horaImpessao,
       noMotorista                                                             AS noMotorista,
       IFNULL(E.sname, '')                                                     AS nomeMotorista
FROM
  T_NOTA                    AS N
    LEFT JOIN T_DEP_ENTREGA AS F
              ON F.storeno_venda = N.loja AND F.pdvno_venda = N.pdv AND F.xano_venda = N.transacao
    LEFT JOIN T_TIPO        AS T
              USING (loja, pdv, transacao)
    LEFT JOIN T_CARD        AS C
              USING (loja, pdv, transacao)
    LEFT JOIN T_DUP         AS D
              USING (loja, pdv, transacao)
    LEFT JOIN T_MOTORISTA   AS M
              USING (loja, pdv, transacao)
    LEFT JOIN sqldados.emp  AS E
              ON E.no = M.noMotorista

