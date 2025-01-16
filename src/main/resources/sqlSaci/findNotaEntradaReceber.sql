USE sqldados;

DROP TABLE IF EXISTS T_IPRD_C;
CREATE TABLE T_IPRD_C
(
  PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey, prdno, grade, qtty, marca
FROM
  sqldados.iprdConferencia AS X
WHERE nfekey != '';

DROP TABLE IF EXISTS T_IPRD_S;
CREATE TABLE T_IPRD_S
(
  PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey, prdno, grade, qtty, 0 AS marca
FROM
  sqldados.invnfe                      AS N
    INNER JOIN sqldados.iprd           AS X
               USING (invno)
    INNER JOIN sqldados.invConferencia AS C
               USING (nfekey)
WHERE nfekey != '';

DROP TABLE IF EXISTS T_MESTRE;
CREATE TABLE T_MESTRE
(
  PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey, prdno, grade
FROM
  T_IPRD_S
UNION
DISTINCT
SELECT nfekey, prdno, grade
FROM
  T_IPRD_C;

DROP TABLE IF EXISTS T_IPRD_M;
CREATE TABLE T_IPRD_M
(
  PRIMARY KEY (nfekey, prdno, grade)
)
SELECT nfekey,
       prdno,
       grade,
       IF(C.marca = 1, IF(S.qtty = C.qtty, 1, 0), 0) AS marca,
       IFNULL(S.qtty, 0)                             AS qttyS,
       IFNULL(C.qtty, 0)                             AS qttyC
FROM
  T_MESTRE
    LEFT JOIN T_IPRD_S AS S
              USING (nfekey, prdno, grade)
    LEFT JOIN T_IPRD_C AS C
              USING (nfekey, prdno, grade);

SELECT IFNULL(NI.invno, 0)                               AS ni,
       IFNULL(NI.storeno, 0)                             AS loja,
       IFNULL(NI.nfname, '')                             AS numero,
       IFNULL(NI.invse, '')                              AS serie,
       IFNULL(V.sname, '')                               AS nomeForn,
       CAST(I.date AS DATE)                              AS emissao,
       CAST(I.date AS DATE)                              AS entrada,
       0                                                 AS valorNota,
       IFNULL(IF(NI.bits & POW(2, 4) = 0, 'N', 'S'), '') AS cancelada,
       I.nfekey                                          AS chave,
       I.marca                                           AS marca
FROM
  sqldados.invConferencia     AS I
    LEFT JOIN T_IPRD_M        AS P
              USING (nfekey)
    LEFT JOIN sqldados.invnfe AS N
              ON N.nfekey = I.nfekey
    LEFT JOIN sqldados.inv    AS NI
              USING (invno)
    LEFT JOIN sqldados.vend   AS V
              ON V.no = NI.vendno
WHERE (I.nfekey = :chave OR :chave = '')
GROUP BY I.nfekey
HAVING (SUM(P.marca = 0) > 0)
    OR (SUM(P.marca IS NULL) > 0)
