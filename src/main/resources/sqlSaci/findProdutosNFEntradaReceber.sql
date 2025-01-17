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
WHERE X.nfekey = :nfekey;

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
WHERE N.nfekey = :nfekey;

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
       IF(C.nfekey IS NULL, -1, IF(C.marca = 1, IF(S.qtty = C.qtty, 1, 0), 0)) AS marca,
       IFNULL(S.qtty, 0)                                                       AS qttyS,
       IFNULL(C.qtty, 0)                                                       AS qttyC
FROM T_MESTRE
         LEFT JOIN T_IPRD_S AS S
                   USING (nfekey, prdno, grade)
         LEFT JOIN T_IPRD_C AS C
                   USING (nfekey, prdno, grade);

SELECT IFNULL(N.storeno, 0)                                     AS loja,
       IFNULL(N.invno, 0)                                       AS ni,
       M.nfekey                                                 AS chave,
       IFNULL(CAST(CONCAT(N.nfname, '/', N.invse) AS CHAR), '') AS nota,
       CAST(TRIM(M.prdno) AS CHAR)                              AS codigo,
       IFNULL(M.grade, '')                                      AS grade,
       TRIM(IFNULL(B.barcode, P.barcode))                       AS barcode,
       TRIM(P.mfno_ref)                                         AS referencia,
       IF(tipoGarantia = 2, garantia, NULL)                     AS mesesGarantia,
       ROUND(qttyPackClosed / 1000)                             AS quantidadePacote,
       TRIM(MID(P.name, 1, 37))                                 AS descricao,
       TRIM(MID(P.name, 37, 3))                                 AS unidade,
       P.mfno                                                   AS vendno,
       IFNULL(F.auxChar1, '')                                   AS fornecedor,
       P.typeno                                                 AS typeno,
       IFNULL(T.name, '')                                       AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                       AS clno,
       IFNULL(cl.name, '')                                      AS clname,
       P.sp / 100                                               AS precoCheio,
       IFNULL(S.ncm, '')                                        AS ncm,
       M.qttyC / 1000                                           AS quantidade,
       X.fob / 100                                              AS preco,
       (X.qtty / 1000) * (X.fob / 100)                          AS total,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR)       AS localizacao,
       X.qtty / 1000                                            AS qttyRef,
       M.marca                                                  AS marca
FROM T_IPRD_M AS M
         INNER JOIN sqldados.prd AS P
                    ON P.no = M.prdno
         LEFT JOIN sqldados.invnfe AS K
                   USING (nfekey)
         LEFT JOIN sqldados.inv AS N
                   USING (invno)
         LEFT JOIN sqldados.iprd AS X
                   USING (invno, prdno, grade)
         LEFT JOIN sqldados.prdbar AS B
                   ON P.no = B.prdno AND B.grade = X.grade
         LEFT JOIN sqldados.prdloc AS L
                   ON L.prdno = P.no AND L.storeno = 4
         LEFT JOIN sqldados.vend AS F
                   ON F.no = P.mfno
         LEFT JOIN sqldados.type AS T
                   ON T.no = P.typeno
         LEFT JOIN sqldados.cl
                   ON cl.no = P.clno
         LEFT JOIN sqldados.spedprd AS S
                   ON P.no = S.prdno
GROUP BY codigo, grade

