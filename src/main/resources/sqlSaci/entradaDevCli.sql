USE sqldados;

SET sql_mode = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA (
  PRIMARY KEY (invno)
)
SELECT I.invno                                                                           AS invno,
       I.storeno                                                                         AS loja,
       CAST(CONCAT(I.nfname, '/', I.invse) AS char)                                      AS notaFiscal,
       CAST(I.date AS DATE)                                                              AS data,
       I.vendno                                                                          AS vendno,
       V.sname                                                                           AS fornecedor,
       I.remarks                                                                         AS remarks,
       I.grossamt / 100                                                                  AS valor,
       IFNULL(NF1.storeno, NF2.storeno)                                                  AS storeno,
       IFNULL(NF1.pdvno, NF2.pdvno)                                                      AS pdvno,
       IFNULL(NF1.xano, NF2.xano)                                                        AS xano,
       IFNULL(NF1.custno, NF2.custno)                                                    AS custno,
       CAST(CONCAT(IFNULL(NF1.nfno, NF2.nfno), '/', IFNULL(NF1.nfse, NF2.nfse)) AS CHAR) AS nfVenda,
       DATE(IFNULL(NF1.issuedate, NF2.issuedate))                                        AS nfData,
       IFNULL(NF1.grossamt / 100, NF2.grossamt / 100)                                    AS nfValor,
       IFNULL(NF1.empno, NF2.empno)                                                      AS empno,
       C.name                                                                            AS cliente,
       E.sname                                                                           AS vendedor,
       @NOTA := TRIM(SUBSTRING_INDEX(
	 TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(REPLACE(I.remarks, 'NFE', 'NF'), 'NF', 2), 'NF', -1)),
	 ' ', 1))                                                                        AS nfRmk,
       SUBSTRING_INDEX(@NOTA, '/', 1) * 1                                                AS nfno,
       MID(SUBSTRING_INDEX(SUBSTRING_INDEX(@NOTA, '/', 2), '/', -1), 1, 2)               AS nfse
FROM sqldados.inv          AS I
  LEFT JOIN sqldados.nf    AS NF1
	      ON (NF1.nfno = I.nfNfno AND NF1.storeno = I.nfStoreno AND NF1.nfse = I.nfNfse)
  LEFT JOIN sqldados.nf    AS NF2
	      ON (NF2.storeno = I.s1 AND NF2.pdvno = I.s2 AND NF2.xano = I.l2)
  LEFT JOIN sqldados.vend  AS V
	      ON V.no = I.vendno
  LEFT JOIN sqldados.custp AS C
	      ON C.no = IFNULL(NF1.custno, NF2.custno)
  LEFT JOIN sqldados.emp   AS E
	      ON E.no = IFNULL(NF1.empno, NF2.empno)
WHERE I.date >= 20220101
  AND I.account = '2.01.25'
  AND (I.storeno = :loja OR :loja = 0)
  AND (I.nfname = :numero OR :numero = '')
  AND (I.invse = :serie OR :serie = '')
  AND ((I.date BETWEEN :dataI AND :dataF) OR :dataI = 0 OR :dataF = 0)
  AND (C.no = :codigoCliente OR :codigoCliente = 0)
  AND (C.name LIKE CONCAT('%', :nomeCliente, '%') OR :nomeCliente = '');

SELECT I.invno,
       I.loja,
       I.notaFiscal,
       I.data,
       I.vendno,
       I.fornecedor,
       I.remarks,
       I.valor,
       IFNULL(I.storeno, N.storeno)                   AS storeno,
       IFNULL(I.pdvno, N.pdvno)                       AS pdvno,
       IFNULL(I.xano, N.xano)                         AS xano,
       IFNULL(I.custno, N.custno)                     AS custno,
       IFNULL(I.nfVenda, CONCAT(I.nfno, '/', I.nfse)) AS nfVenda,
       IFNULL(I.nfData, DATE(N.issuedate))            AS nfData,
       IFNULL(I.nfValor, N.grossamt / 100)            AS nfValor,
       IFNULL(I.empno, N.empno)                       AS empno,
       IFNULL(I.cliente, C.name)                      AS cliente,
       MID(IFNULL(I.vendedor, E.sname), 1, 15)        AS vendedor
FROM T_NOTA                AS I
  LEFT JOIN sqldados.nf    AS N
	      ON I.xano IS NULL AND N.storeno = I.loja AND N.nfno = I.nfno AND N.nfse = I.nfse
  LEFT JOIN sqldados.custp AS C
	      ON C.no = N.custno
  LEFT JOIN sqldados.emp   AS E
	      ON E.no = N.empno
GROUP BY I.invno

