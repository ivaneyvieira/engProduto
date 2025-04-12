USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_GARANTIA;
CREATE TEMPORARY TABLE T_GARANTIA
SELECT *
FROM
  sqldados.produtoEstoqueGarantia
WHERE (numero = :numero OR :numero = 0)
  AND (numloja = :numLoja OR :numLoja = 0)
  AND (data >= :dataInicial OR :dataInicial = 0)
  AND (data <= :dataFinal OR :dataFinal = 0);

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade
FROM
  T_GARANTIA
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ESTOQUE_LOJA;
CREATE TEMPORARY TABLE T_ESTOQUE_LOJA
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM((qtty_atacado + qtty_varejo) / 1000) AS estoqueLoja
FROM
  sqldados.stk
    INNER JOIN T_PRD
               USING (prdno, grade)
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ULTIMO_INVNO;
CREATE TEMPORARY TABLE T_ULTIMO_INVNO
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, MAX(invno) AS invno
FROM
  sqldados.iprd             AS I
    INNER JOIN sqldados.inv AS N
               USING (invno)
    INNER JOIN T_PRD        AS T
               USING (prdno, grade)
WHERE type = 0
  AND I.bits & POW(2, 4) = 0
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ULTIMO_RECEB;
CREATE TEMPORARY TABLE T_ULTIMO_RECEB
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno                      AS prdno,
       grade                      AS grade,
       invno                      AS niReceb,
       CONCAT(nfname, '/', invse) AS nfoReceb,
       CAST(date AS date)         AS entradaReceb,
       vendno                     AS forReceb
FROM
  sqldados.inv AS N
    INNER JOIN T_ULTIMO_INVNO
               USING (invno);

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
  sqldados.prdAdicional   AS P
    INNER JOIN T_GARANTIA AS A
               ON P.storeno = A.numloja
                 AND P.prdno = A.prdno
                 AND P.grade = A.grade
GROUP BY P.storeno, P.prdno, P.grade;

DROP TEMPORARY TABLE IF EXISTS T_BARCODE;
CREATE TEMPORARY TABLE T_BARCODE
(
  PRIMARY KEY (prdno, grade)
)
SELECT P.no                                                                  AS prdno,
       IFNULL(B.grade, '')                                                   AS grade,
       MAX(TRIM(IF(B.grade IS NULL, IFNULL(P2.gtin, P.barcode), B.barcode))) AS codbar
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prd2   AS P2
              ON P.no = P2.prdno
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno AND B.grade != ''
WHERE P.no IN ( SELECT DISTINCT prdno FROM T_GARANTIA )
GROUP BY P.no, B.grade
HAVING codbar != '';

SELECT numero,
       numloja,
       S.sname                  AS lojaSigla,
       data,
       hora,
       usuario,
       A.prdno,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       A.grade,
       L.locApp,
       B.codbar                 AS barcode,
       TRIM(P.mfno_ref)         AS ref,
       estoqueSis,
       ROUND(EL.estoqueLoja)    AS estoqueLoja,
       O.observacao             AS observacao,
       UR.niReceb               AS niReceb,
       UR.nfoReceb              AS nfoReceb,
       UR.entradaReceb          AS entradaReceb,
       UR.forReceb              AS forReceb
FROM
  T_GARANTIA                            AS A
    LEFT JOIN T_ESTOQUE_LOJA            AS EL
              USING (prdno, grade)
    LEFT JOIN T_ULTIMO_RECEB            AS UR
              USING (prdno, grade)
    LEFT JOIN T_BARCODE                 AS B
              ON B.prdno = A.prdno
                AND B.grade = A.grade
    LEFT JOIN produtoObservacaoGarantia AS O
              USING (numero, numloja)
    LEFT JOIN sqldados.store            AS S
              ON S.no = A.numloja
    LEFT JOIN sqldados.prd              AS P
              ON P.no = A.prdno
    LEFT JOIN T_LOC_APP                 AS L
              ON L.storeno = A.numloja
                AND L.prdno = A.prdno
                AND L.grade = A.grade

