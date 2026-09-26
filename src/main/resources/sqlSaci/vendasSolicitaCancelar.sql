USE sqldados;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_REGEXP := CONCAT('.*', REPLACE(@PESQUISA, ' ', ' +'), '.*');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, NULL);

DROP TEMPORARY TABLE IF EXISTS T_NOTAX;
CREATE TEMPORARY TABLE T_NOTAX
(
  INDEX (storeno, pdvno, xano)
)
SELECT storeno,
       pdvno,
       xano,
       paymno,
       eordno,
       nfno,
       nfse,
       issuedate,
       tipo,
       grossamt,
       custno,
       remarks,
       print_remarks,
       custno_addno,
       bits,
       empno,
       xatype,
       cfo
FROM sqldados.nf AS N
WHERE (N.storeno IN (2, 3, 4, 5, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.pdvno = :pdv OR :pdv = 0)
  /*AND N.tipo IN (0, 4)*/
  AND N.status <> 1
  AND N.issuedate BETWEEN SUBDATE(CURRENT_DATE * 1, 1) * 1 AND CURRENT_DATE * 1
ORDER BY storeno, pdvno, xano;

DROP TEMPORARY TABLE IF EXISTS T_TIPO;
CREATE TEMPORARY TABLE T_TIPO
(
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno AS storeno, ordno AS ordno, SUM((E.bits & 2) > 0) AS tipoR, SUM((E.bits & 2) = 0) AS tipoE
FROM sqldados.eoprdf AS E
WHERE (storeno IN (2, 3, 4, 5, 8))
  AND (`date` >= @DT)
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_CARGA;
CREATE TEMPORARY TABLE T_CARGA
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT storeno, pdvno, xano
FROM
  sqldados.nfrprd AS N
    INNER JOIN T_NOTAX
               USING (storeno, pdvno, xano)
WHERE (storenoStk = :loja OR :loja = 0)
  AND storeno != storenoStk
  AND `date` >= SUBDATE(CURRENT_DATE, 30)
  AND optionEntrega % 10 = 4
  AND N.nfse != 3
GROUP BY storeno, pdvno, xano;

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  INDEX (loja, pdv, transacao)
)
SELECT N.storeno                                                   AS loja,
       N.pdvno                                                     AS pdv,
       N.xano                                                      AS transacao,
       N.paymno                                                    AS numMetodo,
       M.sname                                                     AS nomeMetodo,
       M.mult / 10000                                              AS mult,
       N.eordno                                                    AS pedido,
       CAST(N.issuedate AS DATE)                                   AS data,
       N.nfno                                                      AS nfno,
       N.nfse                                                      AS nfse,
       CONCAT(N.nfno, '/', N.nfse)                                 AS nota,
       N.tipo                                                      AS nTipo,
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
       END                                                         AS tipoNf,
       CASE
         WHEN N.remarks LIKE '%RECLASSIFI%' THEN 'RECLASS'
         WHEN N.nfse = 7                    THEN 'ENTREGA_WEB'
         WHEN N.tipo = 0 AND N.nfse >= 10   THEN 'NFCE'
         WHEN N.tipo = 0 AND N.nfse < 10    THEN 'NFE'
         WHEN N.tipo = 1                    THEN 'TRANSFERENCIA'
         WHEN N.tipo = 2                    THEN 'DEVOLUCAO'
         WHEN N.tipo = 3                    THEN IF(N.storeno != :loja AND :loja != 0 AND N.nfse = 3, '', 'SIMP_REME')
         WHEN N.tipo = 4                    THEN IF(IFNULL(T.tipoE, 0) = 0 AND IFNULL(T.tipoR, 0) > 0,
                                                    IF(N.storeno != :loja AND :loja != 0 AND N.nfse = 3, '', 'SIMP_REME'),
                                                    'ENTRE_FUT')
         WHEN N.tipo = 5                    THEN 'RET_DEMON'
         WHEN N.tipo = 6                    THEN 'VENDA_USA'
         WHEN N.tipo = 7 && N.cfo = 5949    THEN 'OUTROS'
         WHEN N.tipo = 8                    THEN 'NF_CF'
         WHEN N.tipo = 9                    THEN 'PERD/CONSER'
         WHEN N.tipo = 10                   THEN 'REPOSICAO'
         WHEN N.tipo = 11                   THEN 'RESSARCI'
         WHEN N.tipo = 12                   THEN 'COMODATO'
         WHEN N.tipo = 13                   THEN 'NF_EMPRESA'
         WHEN N.tipo = 14                   THEN 'BONIFICA'
         WHEN N.tipo = 15                   THEN 'NFE'
                                            ELSE ''
       END                                                         AS tipoNotaSaida,
       (IFNULL(CG.storeno, :loja) != :loja) OR (N.storeno = :loja) AS retiraFutura,
       N.nfse                                                      AS serie,
       SEC_TO_TIME(P.time)                                         AS hora,
       Q.string                                                    AS tipoPgto,
       N.grossamt / 100                                            AS valor,
       N.custno                                                    AS cliente,
       C.name                                                      AS nomeCliente,
       IF(C.cpf_cgc LIKE 'NAO%', '', IFNULL(A.state, C.state1))    AS uf,
       CONCAT(E.no, ' - ', MID(E.sname, 1, 17))                    AS vendedor,
       IFNULL(SUM(V.amt / 100), N.grossamt / 100)                  AS valorTipo,
       CONCAT(N.remarks, ' ', N.print_remarks)                     AS obs
FROM
  T_NOTAX                     AS N
    LEFT JOIN T_CARGA         AS CG
              USING (storeno, pdvno, xano)
    LEFT JOIN sqldados.paym   AS M
              ON N.paymno = M.no
    LEFT JOIN sqldados.ctadd  AS A
              ON A.custno = N.custno AND A.seqno = N.custno_addno
    LEFT JOIN sqlpdv.pxa      AS P
              USING (storeno, pdvno, xano)
    LEFT JOIN sqlpdv.pxaval   AS V
              USING (storeno, pdvno, xano)
    LEFT JOIN T_TIPO          AS T
              ON N.storeno = T.storeno AND N.eordno = T.ordno
    LEFT JOIN sqldados.card      c
              ON N.bits = c.bits
    LEFT JOIN sqldados.custp  AS C
              ON C.no = N.custno
    LEFT JOIN sqldados.emp    AS E
              ON E.no = N.empno
    LEFT JOIN sqldados.query1 AS Q
              ON Q.no_short = IF(N.xatype = 999, V.xatype, N.xatype)
GROUP BY N.storeno, N.pdvno, N.xano, IF(N.xatype = 999, V.xatype, N.xatype)
HAVING (@PESQUISA = '' OR pedido = @PESQUISA_INT OR pdv = @PESQUISA_INT OR nota LIKE @PESQUISA_START OR
        tipoNotaSaida LIKE @PESQUISA_LIKE OR tipoPgto LIKE @PESQUISA_LIKE OR cliente LIKE @PESQUISA_INT OR
        UPPER(obs) REGEXP CONCAT('NI[^0-9A-Z]*', @PESQUISA_INT) OR nomeCliente LIKE @PESQUISA_LIKE OR
        vendedor LIKE @PESQUISA_LIKE OR transacao = @PESQUISA_INT OR M.sname REGEXP @PESQUISA_REGEXP)
ORDER BY N.storeno, N.pdvno, N.xano, IF(N.xatype = 999, V.xatype, N.xatype);

DROP TEMPORARY TABLE IF EXISTS T_CHAVE;
CREATE TEMPORARY TABLE T_CHAVE
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja, pdv, transacao, nfno, nfse, data AS dataVenda
FROM T_NOTA
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
       pdv,
       transacao,
       numMetodo,
       nomeMetodo,
       mult,
       pedido,
       data,
       nota,
       tipoNf,
       tipoNotaSaida,
       retiraFutura,
       serie,
       nTipo,
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
