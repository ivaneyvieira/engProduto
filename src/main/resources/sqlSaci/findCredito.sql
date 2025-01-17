USE sqldados;

SET sql_mode = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_DEV;
CREATE TEMPORARY TABLE T_DEV
(
  PRIMARY KEY (custno, invno)
)
SELECT DISTINCT H.custno, MAX(H.invno) AS invno
FROM
  sqldados.cthcr2           AS H
  LEFT JOIN sqldados.inv    AS I
    USING (invno)
  INNER JOIN sqldados.custp AS C
    ON C.no = H.custno
WHERE
    invno > 0
AND saldoDevolucao != 0
GROUP BY
  H.custno;


DROP TEMPORARY TABLE IF EXISTS T_VENDA;
CREATE TEMPORARY TABLE T_VENDA
SELECT N.storeno                 AS loja,
       N.pdvno                   AS pdv,
       N.xano                    AS xano,
       CONCAT(nfno, '/', nfse)   AS nfVenda,
       CAST(N.issuedate AS DATE) AS data,
       N.grossamt / 100          AS nfValor,
       N.custno                  AS custno,
       N.custno                  AS cliente,
       C.name                    AS clienteNome,
       N.grossamt / 100          AS valorVenda,
       D.invno,
       CASE
         WHEN N.remarks REGEXP 'NI *[0-9]+'       THEN N.remarks
         WHEN N.print_remarks REGEXP 'NI *[0-9]+' THEN N.print_remarks
                                                  ELSE ''
       END                       AS obsNI
FROM
  sqldados.nf               AS N
  INNER JOIN sqldados.custp AS C
    ON C.no = N.custno
  INNER JOIN T_DEV          AS D
    ON (N.print_remarks LIKE CONCAT('%NI ', D.invno, '%') OR N.remarks LIKE CONCAT('%NI ', D.invno, '%'))
WHERE
    (N.print_remarks REGEXP 'NI *[0-9]+' OR N.remarks REGEXP 'NI *[0-9]+')
AND N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8)
AND N.status <> 1;

DROP TEMPORARY TABLE IF EXISTS T_LOJ;
CREATE TEMPORARY TABLE T_LOJ
(
  PRIMARY KEY (custno)
)
SELECT 200 AS custno, 2 AS storeno
UNION ALL
SELECT 300 AS custno, 3 AS storeno
UNION ALL
SELECT 400 AS custno, 4 AS storeno
UNION ALL
SELECT 500 AS custno, 5 AS storeno
UNION ALL
SELECT 600 AS custno, 6 AS storeno
UNION ALL
SELECT 700 AS custno, 7 AS storeno
UNION ALL
SELECT 800 AS custno, 8 AS storeno;

SELECT C.no                                AS codigo,
       C.name                              AS nome,
       ROUND(C.saldoDevolucao / 100, 2)    AS vlCredito,
       I.invno                             AS invno,
       IFNULL(I.storeno, L.storeno)        AS loja,
       SUBSTRING_INDEX(I.remarks, ')', -1) AS tipo,
       I.invno                             AS ni,
       CONCAT(I.nfname, '/', I.invse)      AS nfDev,
       CAST(I.issue_date AS DATE)          AS dtDev,
       I.vendno                            AS vendno,
       I.remarks                           AS remarks,
       V.sname                             AS fornecedor,
       I.grossamt / 100                    AS valorDev,
       N.nfVenda                           AS nfVenda,
       CAST(N.data AS DATE)                AS dtVenda,
       N.custno                            AS custnoVenda,
       clienteNome                         AS clienteVenda,
       valorVenda                          AS valorVenda
FROM
  sqldados.custp          AS C
  LEFT JOIN T_DEV         AS D
    ON C.no = D.custno
  LEFT JOIN sqldados.inv  AS I
    ON D.invno = I.invno
  LEFT JOIN sqldados.vend AS V
    ON I.vendno = V.no
  LEFT JOIN T_VENDA       AS N
    ON N.invno = D.invno
  LEFT JOIN T_LOJ         AS L
    ON C.no = L.custno
WHERE
    C.saldoDevolucao != 0
AND (@PESQUISA = '' OR C.no = @PESQUISANUM OR C.name LIKE @PESQUISALIKE)
ORDER BY
  C.no, D.invno