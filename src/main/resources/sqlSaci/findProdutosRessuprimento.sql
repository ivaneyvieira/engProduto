USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_LOC1;
CREATE TEMPORARY TABLE T_LOC1
(
  PRIMARY KEY (prdno, grade)
)
SELECT L.prdno                                                                              AS prdno,
       L.grade                                                                              AS grade,
       CAST(MID(COALESCE(A1.localizacao, A2.localizacao, L.localizacao, ''), 1, 4) AS CHAR) AS localizacao
FROM sqldados.prdloc AS L
       LEFT JOIN sqldados.prdAdicional AS A1
                 USING (storeno, prdno, grade)
       LEFT JOIN sqldados.prdAdicional AS A2
                 USING (storeno, prdno)
WHERE storeno = 4
GROUP BY L.prdno, L.grade;

DROP TEMPORARY TABLE IF EXISTS T_LOC2;
CREATE TEMPORARY TABLE T_LOC2
(
  PRIMARY KEY (prdno, grade)
)
SELECT *
FROM T_LOC1;

SELECT *
FROM (SELECT ordno                                  AS ordno,
             CAST(TRIM(P.no) AS CHAR)               AS codigo,
             IFNULL(X.grade, '')                    AS grade,
             TRIM(IFNULL(B.barcode, P.barcode))     AS barcode,
             TRIM(MID(P.name, 1, 37))               AS descricao,
             P.mfno                                 AS vendno,
             IFNULL(F.auxChar1, '')                 AS fornecedor,
             P.typeno                               AS typeno,
             IFNULL(T.name, '')                     AS typeName,
             CAST(LPAD(P.clno, 6, '0') AS CHAR)     AS clno,
             IFNULL(cl.name, '')                    AS clname,
             P.m6                                   AS altura,
             P.m4                                   AS comprimento,
             P.m5                                   AS largura,
             P.sp / 100                             AS precoCheio,
             X.qtty                                 AS quantidade,
             X.cost                                 AS preco,
             (X.qtty * X.mult / 1000) * X.cost      AS total,
             X.auxShort4                            AS marca,
             X.auxShort3 != 0                       AS selecionado,
             X.auxLong4                             AS posicao,
             L.localizacao                          AS localizacao,
             ROUND(IFNULL(S.qtty_varejo, 0) / 1000) AS estoque
      FROM sqldados.prd AS P
             INNER JOIN sqldados.oprd AS X
                        ON P.no = X.prdno
             INNER JOIN sqldados.ords AS N
                        ON N.storeno = X.storeno AND N.no = X.ordno
             LEFT JOIN sqldados.stk AS S
                       ON S.prdno = X.prdno AND S.grade = X.grade AND S.storeno = 4
             LEFT JOIN sqldados.prdbar AS B
                       ON P.no = B.prdno AND B.grade = X.grade
             LEFT JOIN T_LOC1 AS L
                       ON X.prdno = L.prdno
                         AND X.grade = L.grade
             LEFT JOIN sqldados.vend AS F
                       ON F.no = P.mfno
             LEFT JOIN sqldados.type AS T
                       ON T.no = P.typeno
             LEFT JOIN sqldados.cl
                       ON cl.no = P.clno
      WHERE X.storeno = 1
        AND X.ordno = :ordno
        AND (X.auxShort4 = :marca OR :marca = 999)
        AND (L.localizacao IN (:locais) OR 'TODOS' IN (:locais))
        AND (L.localizacao = :locApp)
      GROUP BY codigo, grade
      UNION
      DISTINCT
      SELECT ordno                                  AS ordno,
             CAST(TRIM(P.no) AS CHAR)               AS codigo,
             IFNULL(X.grade, '')                    AS grade,
             TRIM(IFNULL(B.barcode, P.barcode))     AS barcode,
             TRIM(MID(P.name, 1, 37))               AS descricao,
             P.mfno                                 AS vendno,
             IFNULL(F.auxChar1, '')                 AS fornecedor,
             P.typeno                               AS typeno,
             IFNULL(T.name, '')                     AS typeName,
             CAST(LPAD(P.clno, 6, '0') AS CHAR)     AS clno,
             IFNULL(cl.name, '')                    AS clname,
             P.m6                                   AS altura,
             P.m4                                   AS comprimento,
             P.m5                                   AS largura,
             P.sp / 100                             AS precoCheio,
             X.qtty                                 AS quantidade,
             X.cost                                 AS preco,
             (X.qtty * X.mult / 1000) * X.cost      AS total,
             X.auxShort4                            AS marca,
             X.auxShort3 != 0                       AS selecionado,
             X.auxLong4                             AS posicao,
             L.localizacao                          AS localizacao,
             ROUND(IFNULL(S.qtty_varejo, 0) / 1000) AS estoque
      FROM sqldados.prd AS P
             INNER JOIN sqldados.oprdRessu AS X
                        ON P.no = X.prdno
             INNER JOIN sqldados.ordsRessu AS N
                        ON N.storeno = X.storeno AND N.no = X.ordno
             LEFT JOIN sqldados.stk AS S
                       ON S.prdno = X.prdno AND S.grade = X.grade AND S.storeno = 4
             LEFT JOIN sqldados.prdbar AS B
                       ON P.no = B.prdno AND B.grade = X.grade
             LEFT JOIN T_LOC2 AS L
                       ON X.prdno = L.prdno
                         AND X.grade = L.grade
             LEFT JOIN sqldados.vend AS F
                       ON F.no = P.mfno
             LEFT JOIN sqldados.type AS T
                       ON T.no = P.typeno
             LEFT JOIN sqldados.cl
                       ON cl.no = P.clno
      WHERE X.storeno = 1
        AND X.ordno = :ordno
        AND (X.auxShort4 = :marca OR :marca = 999)
        AND (L.localizacao IN (:locais) OR 'TODOS' IN (:locais))
        AND (L.localizacao = :locApp)
      GROUP BY codigo, grade) AS D
GROUP BY codigo, grade