USE sqldados;

SET sql_mode = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_DEV;
CREATE TEMPORARY TABLE T_DEV
(
  PRIMARY KEY (custno)
)
SELECT custno, MAX(invno) AS invno
FROM sqldados.cthcr2
WHERE invno > 0
GROUP BY custno;

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



SELECT no                                  AS codigo,
       name                                AS nome,
       ROUND(saldoDevolucao / 100, 2)      AS vlCredito,
       IFNULL(I.storeno, L.storeno)        AS loja,
       SUBSTRING_INDEX(I.remarks, ')', -1) AS tipo
FROM sqldados.custp AS C
       LEFT JOIN T_DEV AS D
                 ON C.no = D.custno
       LEFT JOIN sqldados.inv AS I
                 ON D.invno = I.invno
       LEFT JOIN T_LOJ AS L
                 ON C.no = L.custno
WHERE saldoDevolucao != 0
  AND (@PESQUISA = '' OR no = @PESQUISANUM OR name LIKE @PESQUISALIKE)