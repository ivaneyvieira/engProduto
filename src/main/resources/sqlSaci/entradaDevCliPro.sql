USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA (
  PRIMARY KEY (invno)
)
SELECT I.invno                                                                           AS invno,
       I.storeno                                                                         AS codLoja,
       S.sname                                                                           AS loja,
       CAST(CONCAT(I.nfname, '/', I.invse) AS char)                                      AS notaFiscal,
       CAST(I.date AS DATE)                                                              AS data,
       I.vendno                                                                          AS vendno,
       I.remarks                                                                         AS remarks,
       IFNULL(NF1.storeno, NF2.storeno)                                                  AS storeno,
       IFNULL(NF1.pdvno, NF2.pdvno)                                                      AS pdvno,
       IFNULL(NF1.xano, NF2.xano)                                                        AS xano,
       CAST(CONCAT(IFNULL(NF1.nfno, NF2.nfno), '/', IFNULL(NF1.nfse, NF2.nfse)) AS CHAR) AS nfVenda,
       DATE(IFNULL(NF1.issuedate, NF2.base_iss_amt))                                     AS nfData,
       IFNULL(NF1.custno, NF2.custno)                                                    AS custno,
       IFNULL(NF1.empno, NF2.empno)                                                      AS empno,
       C.name                                                                            AS cliente,
       E.sname                                                                           AS vendedor,
       @NOTA := TRIM(SUBSTRING_INDEX(
	 TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(REPLACE(I.remarks, 'NFE', 'NF'), 'NF', 2), 'NF', -1)),
	 ' ', 1))                                                                        AS nfRmk,
       SUBSTRING_INDEX(@NOTA, '/', 1) * 1                                                AS nfno,
       MID(SUBSTRING_INDEX(SUBSTRING_INDEX(@NOTA, '/', 2), '/', -1), 1, 2)               AS nfse
FROM sqldados.inv          AS I
  LEFT JOIN sqldados.store AS S
	      ON S.no = I.storeno
  LEFT JOIN sqldados.nf    AS NF1
	      ON (NF1.nfno = I.nfNfno AND NF1.storeno = I.nfStoreno AND NF1.nfse = I.nfNfse)
  LEFT JOIN sqldados.nf    AS NF2
	      ON (NF2.storeno = I.s1 AND NF2.pdvno = I.s2 AND NF2.xano = I.l2)
  LEFT JOIN sqldados.custp AS C
	      ON C.no = IFNULL(NF1.custno, NF2.custno)
  LEFT JOIN sqldados.emp   AS E
	      ON E.no = IFNULL(NF1.empno, NF2.empno)
WHERE I.date >= 20220101
  AND I.account = '2.01.25'
  AND I.invno = :invno;

SELECT I.invno,
       I.codLoja,
       I.loja,
       I.notaFiscal,
       DATE_FORMAT(I.data, '%d/%m/%Y')                                               AS data,
       I.vendno,
       I.remarks,
       IFNULL(I.storeno, N.storeno)                                                  AS storeno,
       IFNULL(I.pdvno, N.pdvno)                                                      AS pdvno,
       IFNULL(I.xano, N.xano)                                                        AS xano,
       IFNULL(I.nfVenda, CONCAT(I.nfno, '/', I.nfse))                                AS nfVenda,
       IFNULL(DATE_FORMAT(nfData, '%d/%m/%Y'), DATE_FORMAT(N.issuedate, '%d/%m/%Y')) AS nfData,
       IFNULL(I.custno, N.custno)                                                    AS custno,
       IFNULL(I.empno, N.empno)                                                      AS empno,
       V.sname                                                                       AS fornecedor,
       X.prdno                                                                       AS prdno,
       TRIM(X.prdno)                                                                 AS codigo,
       X.grade                                                                       AS grade,
       X.qtty / 1000                                                                 AS quantidade,
       X.fob / 100                                                                   AS valorUnitario,
       (X.qtty / 1000) * (X.fob / 100)                                               AS valorTotal,
       TRIM(MID(P.name, 1, 37))                                                      AS descricao,
       IFNULL(I.cliente, C.name)                                                     AS cliente,
       MID(IFNULL(I.vendedor, E.sname), 1, 15)                                       AS vendedor
FROM T_NOTA                 AS I
  LEFT JOIN  sqldados.nf    AS N
	       ON N.storeno = I.codLoja AND N.nfno = I.nfno AND N.nfse = I.nfse
  LEFT JOIN  sqldados.custp AS C
	       ON C.no = N.custno
  LEFT JOIN  sqldados.emp   AS E
	       ON E.no = N.empno
  INNER JOIN sqldados.iprd  AS X
	       ON I.invno = X.invno
  LEFT JOIN  sqldados.prd   AS P
	       ON P.no = X.prdno
  LEFT JOIN  sqldados.vend  AS V
	       ON V.no = I.vendno
GROUP BY I.invno, X.prdno, X.grade


