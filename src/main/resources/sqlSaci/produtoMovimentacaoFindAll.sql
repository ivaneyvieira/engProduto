USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_ACERTO;
CREATE TEMPORARY TABLE T_ACERTO
SELECT M.numero,
       M.numloja,
       M.data,
       M.hora,
       M.usuario,
       M.prdno,
       M.grade,
       M.gravado,
       M.gravadoLogin,
       M.login,
       M.movimentacao,
       M.estoque
FROM
  sqldados.produtoMovimentacao AS M
WHERE (numero = :numero OR :numero = 0)
  AND (numloja = :numLoja OR :numLoja = 0)
  AND (data >= :dataInicial OR :dataInicial = 0)
  AND (data <= :dataFinal OR :dataFinal = 0);

DROP TEMPORARY TABLE IF EXISTS T_LOC_APP;
CREATE TEMPORARY TABLE T_LOC_APP
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT P.storeno,
       P.prdno,
       P.grade,
       P.localizacao AS locApp
FROM
  sqldados.prdAdicional AS P
    INNER JOIN T_ACERTO AS A
               ON P.storeno = A.numloja
                 AND P.prdno = A.prdno
                 AND P.grade = A.grade
GROUP BY P.storeno, P.prdno, P.grade;

DROP TEMPORARY TABLE IF EXISTS T_BARCODE;
CREATE TEMPORARY TABLE T_BARCODE
(
  PRIMARY KEY (prdno, grade)
)
SELECT P.no                                                           AS prdno,
       IFNULL(B.grade, '')                                            AS grade,
       MAX(TRIM(IF(B.grade IS NULL,
                   IFNULL(IF(LENGTH(TRIM(P.barcode)) = 13,
                             P.barcode, NULL), P2.gtin), B.barcode))) AS codbar
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prd2   AS P2
              ON P.no = P2.prdno
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno AND B.grade != '' AND LENGTH(TRIM(B.barcode)) = 13
WHERE P.no IN ( SELECT DISTINCT prdno FROM T_ACERTO )
GROUP BY P.no, B.grade
HAVING codbar != '';

SELECT numero,
       numloja,
       S.sname                  AS lojaSigla,
       data,
       hora,
       login,
       usuario,
       A.prdno,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       A.grade,
       P.mfno                   AS codFor,
       L.locApp,
       B.codbar                 AS barcode,
       TRIM(P.mfno_ref)         AS ref,
       gravadoLogin,
       gravado,
       movimentacao,
       A.estoque
FROM
  T_ACERTO                   AS A
    LEFT JOIN T_BARCODE      AS B
              ON B.prdno = A.prdno
                AND B.grade = A.grade
    LEFT JOIN sqldados.store AS S
              ON S.no = A.numloja
    LEFT JOIN sqldados.prd   AS P
              ON P.no = A.prdno
    LEFT JOIN T_LOC_APP      AS L
              ON L.storeno = A.numloja
                AND L.prdno = A.prdno
                AND L.grade = A.grade

