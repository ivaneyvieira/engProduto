USE sqldados;

SET sql_mode = '';

DO @PESQUISA := :query;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno)
)
SELECT I.invno                                                                           AS invno,
       IF(I.usernoLast = 0, I.usernoFirst, I.usernoLast)                                 AS userno,
       I.storeno                                                                         AS loja,
       S.otherName                                                                       AS nomeLoja,
       CAST(CONCAT(I.nfname, '/', I.invse) AS CHAR)                                      AS notaFiscal,
       CAST(I.date AS DATE)                                                              AS data,
       I.vendno                                                                          AS vendno,
       V.sname                                                                           AS fornecedor,
       I.remarks                                                                         AS remarks,
       I.grossamt / 100                                                                  AS valor,
       IFNULL(NF1.storeno, NF2.storeno)                                                  AS storeno,
       IFNULL(NF1.pdvno, NF2.pdvno)                                                      AS pdvno,
       IFNULL(NF1.xano, NF2.xano)                                                        AS xano,
       IFNULL(NF1.custno, NF2.custno)                                                    AS custno,
       IFNULL(NF1.cfo, NF2.cfo)                                                          AS cfo,
       CAST(CONCAT(IFNULL(NF1.nfno, NF2.nfno), '/', IFNULL(NF1.nfse, NF2.nfse)) AS CHAR) AS nfVenda,
       DATE(IFNULL(NF1.issuedate, NF2.issuedate))                                        AS nfData,
       IFNULL(NF1.grossamt / 100, NF2.grossamt / 100)                                    AS nfValor,
       IFNULL(NF1.empno, NF2.empno)                                                      AS empno,
       C.name                                                                            AS cliente,
       E.name                                                                            AS vendedor,
       @NOTA := TRIM(SUBSTRING_INDEX(
           TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(REPLACE(I.remarks, 'NFE', 'NF'), 'NF', 2), 'NF', -1)),
           ' ', 1))                                                                      AS nfRmk,
       SUBSTRING_INDEX(@NOTA, '/', 1) * 1                                                AS nfno,
       MID(SUBSTRING_INDEX(SUBSTRING_INDEX(@NOTA, '/', 2), '/', -1), 1, 2)               AS nfse,
       I.c9                                                                              AS impressora
FROM sqldados.inv AS I
       LEFT JOIN sqldados.nf AS NF1
                 ON (NF1.nfno = I.nfNfno AND NF1.storeno = I.nfStoreno AND NF1.nfse = I.nfNfse)
       LEFT JOIN sqldados.nf AS NF2
                 ON (NF2.storeno = I.s1 AND NF2.pdvno = I.s2 AND NF2.xano = I.l2)
       LEFT JOIN sqldados.vend AS V
                 ON V.no = I.vendno
       LEFT JOIN sqldados.custp AS C
                 ON C.no = IFNULL(NF1.custno, NF2.custno)
       LEFT JOIN sqldados.emp AS E
                 ON E.no = IFNULL(NF1.empno, NF2.empno)
       LEFT JOIN sqldados.store AS S
                 ON S.no = I.storeno
WHERE I.account = '2.01.25'
  AND I.bits & POW(2, 4) = 0
  AND (I.storeno = :loja OR :loja = 0)
  AND (I.date >= :dataI OR :dataI = 0)
  AND (I.date <= :dataF OR :dataF = 0)
  AND (I.date >= :dataLimiteInicial)
  AND CASE :impresso
        WHEN 'S' THEN I.c9 != ''
        WHEN 'N' THEN I.c9 = ''
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END;

SELECT I.invno,
       I.loja,
       I.nomeLoja,
       I.notaFiscal,
       I.data,
       IF(LOCATE('/', impressora) > 0,
          SUBSTRING_INDEX(impressora, '/', -1),
          TIME_FORMAT(CURRENT_TIME, '%H:%i'))         AS hora,
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
       IFNULL(I.cfo, N.cfo)                           AS cfo,
       TRIM(IFNULL(I.vendedor, E.name))               AS vendedor,
       SUBSTRING_INDEX(impressora, '/', 1)            AS impressora,
       U.name                                         AS userName
FROM T_NOTA AS I
       LEFT JOIN sqldados.nf AS N
                 ON I.xano IS NULL AND N.storeno = I.loja AND N.nfno = I.nfno AND N.nfse = I.nfse
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
       LEFT JOIN sqldados.emp AS E
                 ON E.no = N.empno
       LEFT JOIN sqldados.users AS U
                 ON U.no = I.userno
WHERE (@PESQUISA = '' OR
       I.invno = @PESQUISANUM OR
       I.loja = @PESQUISANUM OR
       I.notaFiscal LIKE @PESQUISASTART OR
       I.vendno = @PESQUISANUM OR
       I.fornecedor LIKE @PESQUISALIKE OR
       nfVenda LIKE @PESQUISASTART OR
       IFNULL(I.custno, N.custno) = @PESQUISANUM OR
       IFNULL(I.cliente, C.name) LIKE @PESQUISALIKE)
  AND IFNULL(I.xano, N.xano) IS NOT NULL
GROUP BY I.invno
