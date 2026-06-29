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
       CONCAT(N.remarks, ' ', N.print_remarks)                  AS obs
FROM
  sqldados.nf                  AS N
    LEFT JOIN  sqldados.paym   AS M
               ON N.paymno = M.no
    LEFT JOIN  sqldados.ctadd  AS A
               ON A.custno = N.custno AND A.seqno = N.custno_addno
    LEFT JOIN  sqlpdv.pxa      AS P
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.custp  AS C
               ON C.no = N.custno
    INNER JOIN sqldados.emp    AS E
               ON E.no = N.empno
    LEFT JOIN  sqldados.query1 AS Q
               ON Q.no_short = N.xatype
WHERE (N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.tipo IN (0, 4)
  AND N.status <> 1
GROUP BY N.storeno, N.pdvno, N.xano
ORDER BY N.storeno, N.pdvno, N.xano;

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
       hora,
       valor,
       cliente,
       nomeCliente,
       uf,
       vendedor,
       obs
FROM T_NOTA AS N
WHERE (@PESQUISA = '' OR pedido = @PESQUISA_INT OR pdv = @PESQUISA_INT OR nota LIKE @PESQUISA_START OR
       tipoNf LIKE @PESQUISA_LIKE OR cliente LIKE @PESQUISA_INT OR
       UPPER(obs) REGEXP CONCAT('NI[^0-9A-Z]*', @PESQUISA_INT) OR nomeCliente LIKE @PESQUISA_LIKE OR
       vendedor LIKE @PESQUISA_LIKE OR transacao = @PESQUISA_INT OR nomeMetodo REGEXP @PESQUISA_REGEXP)
