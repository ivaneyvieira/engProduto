USE
sqldados;

DROP TABLE IF EXISTS T_IPRD_C;
CREATE TABLE T_IPRD_C
(
    PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey,
       prdno,
       grade,
       qtty,
       marca
FROM sqldados.iprdConferencia AS X
WHERE nfekey != '';

DROP TABLE IF EXISTS T_IPRD_S;
CREATE TABLE T_IPRD_S
(
    PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey,
       prdno,
       grade,
       qtty,
       0 AS marca
FROM sqldados.invnfe AS N
         INNER JOIN sqldados.iprd AS X
                    USING (invno)
         INNER JOIN sqldados.invConferencia AS C
                    USING (nfekey)
WHERE nfekey != '';

DROP TABLE IF EXISTS T_MESTRE;
CREATE TABLE T_MESTRE
(
    PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey,
       prdno,
       grade
FROM T_IPRD_S
UNION
DISTINCT
SELECT nfekey,
       prdno,
       grade
FROM T_IPRD_C;

DROP TABLE IF EXISTS T_IPRD_M;
CREATE TABLE T_IPRD_M
(
    PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey,
       prdno,
       grade,
       IF(C.marca = 1, IF(S.qtty = C.qtty, 1, 0), 0) AS marca,
       IFNULL(S.qtty, 0) AS qttyS,
       IFNULL(C.qtty, 0) AS qttyC
FROM T_MESTRE
         LEFT JOIN T_IPRD_S AS S
                   USING (nfekey, prdno, grade)
         LEFT JOIN T_IPRD_C AS C
                   USING (nfekey, prdno, grade);

SELECT I.invno AS ni,
       I.storeno AS loja,
       I.nfname AS numero,
       I.invse AS serie,
       I.vendno AS fornecedor,
       V.sname AS nomeForn,
       CAST(I.issue_date AS DATE) AS emissao,
       CAST(I.date AS DATE) AS entrada,
       I.grossamt / 100 AS valorNota,
       IF(I.bits & POW(2, 4) = 0, 'N', 'S') AS cancelada,
       N.nfekey AS chave,
       2 AS marca
FROM sqldados.inv AS I
         INNER JOIN sqldados.invnfe AS N
                    USING (invno)
         INNER JOIN sqldados.vend AS V
                    ON V.no = I.vendno
         INNER JOIN T_IPRD_M AS P
                    ON P.nfekey = N.nfekey
WHERE I.storeno IN (2, 3, 4, 5)
  AND date >= 20220101
  AND I.cfo NOT IN (1551
    , 2551
    , 1556
    , 2556)
  AND (I.storeno = :loja
   OR :loja = 0)
  AND (I.invno = :ni
   OR :ni = 0)
  AND (I.nfname = :nfno
   OR :nfno = '')
  AND (I.invse = :nfse
   OR :nfse = '')
  AND (I.vendno = :vendno
   OR :vendno = 0)
  AND (N.nfekey = :chave
   OR :chave = '')
GROUP BY I.invno
HAVING SUM (P.marca = 0) = 0