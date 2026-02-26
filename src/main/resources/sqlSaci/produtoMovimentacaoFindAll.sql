USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_ACERTO;
CREATE TEMPORARY TABLE T_ACERTO
SELECT M.numero,
       M.numloja,
       M.data,
       M.hora,
       M.prdno,
       M.grade,
       M.noLogin,
       M.noGravado,
       M.noEntregue,
       M.noRecebido,
       M.movimentacao,
       M.estoque,
       M.noRota,
       CAST(IF(dataEntrege = 0, NULL, dataEntrege) AS date)   AS dataEntrege,
       SEC_TO_TIME(horaEntrege)                               AS horaEntrege,
       CAST(IF(dataRecebido = 0, NULL, dataRecebido) AS date) AS dataRecebido,
       SEC_TO_TIME(horaRecebido)                              AS horaRecebido
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
              ON P.no = B.prdno
                AND B.grade != ''
                AND LENGTH(TRIM(B.barcode)) = 13
WHERE P.no IN ( SELECT DISTINCT prdno FROM T_ACERTO )
GROUP BY P.no, B.grade
HAVING codbar != '';

SELECT numero                   AS numero,
       numloja                  AS numloja,
       S.sname                  AS lojaSigla,
       data                     AS data,
       hora                     AS hora,
       UL.no                    AS noLogin,
       UL.login                 AS login,
       UL.name                  AS usuario,
       A.prdno                  AS prdno,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       A.grade                  AS grade,
       P.mfno                   AS codFor,
       L.locApp                 AS locApp,
       B.codbar                 AS barcode,
       TRIM(P.mfno_ref)         AS ref,
       UG.no                    AS noGravado,
       UG.login                 AS gravadoLogin,
       EE.no                    AS noEntregue,
       EE.login                 AS entregue,
       EE.name                  AS entregueNome,
       ER.no                    AS noRecebido,
       ER.login                 AS recebido,
       ER.name                  AS recebidoNome,
       A.movimentacao           AS movimentacao,
       A.estoque                AS estoque,
       A.noRota                 AS noRota,
       dataEntrege              AS dataEntrege,
       horaEntrege              AS horaEntrege,
       dataRecebido             AS dataRecebido,
       horaRecebido             AS horaRecebido

FROM
  T_ACERTO                   AS A
    LEFT JOIN sqldados.users AS UL
              ON UL.no = A.noLogin
    LEFT JOIN sqldados.users AS UG
              ON UG.no = A.noGravado
    LEFT JOIN sqldados.users AS EE
              ON EE.no = A.noEntregue
    LEFT JOIN sqldados.users AS ER
              ON ER.no = noRecebido
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

