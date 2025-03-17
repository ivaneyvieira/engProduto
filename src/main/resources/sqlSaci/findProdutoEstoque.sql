USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_LOC_SACI;
CREATE TEMPORARY TABLE T_LOC_SACI
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, GROUP_CONCAT(DISTINCT localizacao ORDER BY 1) AS locSaci
FROM
  sqldados.prdloc
WHERE localizacao <> ''
  AND storeno = 4
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_LOC_APP;
CREATE TEMPORARY TABLE T_LOC_APP
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno,
       grade,
       MAX(localizacao)                                      AS locApp,
       MAX(dataInicial)                                      AS dataInicial,
       MAX(IF(dataUpdate * 1 = 0, NULL, dataUpdate))         AS dataUpdate,
       MAX(kardec)                                           AS kardec,
       MAX(IF(dataObservacao * 1 = 0, NULL, dataObservacao)) AS dataObservacao,
       MAX(observacao)                                       AS observacao,
       MAX(estoque)                                          AS estoque,
       MAX(estoqueCD)                                        AS estoqueCD,
       MAX(estoqueLoja)                                      AS estoqueLoja
FROM
  sqldados.prdAdicional
WHERE storeno = 4
  AND (prdno = :prdno OR :prdno = '')
GROUP BY prdno, grade;

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
GROUP BY P.no, B.grade
HAVING codbar != '';

DROP TEMPORARY TABLE IF EXISTS T_ULT_ACERTO;
CREATE TEMPORARY TABLE T_ULT_ACERTO
(
  PRIMARY KEY (numloja, prdno, grade)
)
SELECT numloja, prdno, grade, MAX(numero) AS numero
FROM
  sqldados.produtoEstoqueAcerto A
GROUP BY numloja, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ACERTO;
CREATE TEMPORARY TABLE T_ACERTO
(
  PRIMARY KEY (numloja, prdno, grade)
)
SELECT numloja, prdno, grade, numero, processado
FROM
  sqldados.produtoEstoqueAcerto A
    INNER JOIN T_ULT_ACERTO
               USING (numloja, prdno, grade, numero);

DROP TEMPORARY TABLE IF EXISTS temp_pesquisa;
CREATE TEMPORARY TABLE temp_pesquisa
SELECT 4                                                                              AS loja,
       S.sname                                                                        AS lojaSigla,
       E.prdno                                                                        AS prdno,
       TRIM(P.no) * 1                                                                 AS codigo,
       TRIM(MID(P.name, 1, 37))                                                       AS descricao,
       TRIM(MID(P.name, 38, 3))                                                       AS unidade,
       E.grade                                                                        AS grade,
       ROUND(P.qttyPackClosed / 1000)                                                 AS embalagem,
       SUM(CASE
             WHEN P.name LIKE 'SVS E-COLOR%' THEN TRUNCATE(
                 ROUND(IF(E.storeno = 4, E.qtty_atacado + E.qtty_varejo, 0) / 1000) / 5800, 2)
             WHEN P.name LIKE 'VRC COLOR%'   THEN TRUNCATE(
                 ROUND(IF(E.storeno = 4, E.qtty_atacado + E.qtty_varejo, 0) / 1000) / 1000, 2)
                                             ELSE TRUNCATE(
                                                 ROUND(IF(E.storeno = 4, E.qtty_atacado + E.qtty_varejo, 0) / 1000) /
                                                 (P.qttyPackClosed / 1000), 0)
           END)                                                                       AS qtdEmbalagem,
       IFNULL(A.estoque, 0)                                                           AS estoque,
       A.locApp                                                                       AS locApp,
       V.no                                                                           AS codForn,
       V.sname                                                                        AS fornecedor,
       ROUND(SUM(IF(E.storeno = 4, E.qtty_atacado + E.qtty_varejo, 0)) / 1000)        AS saldo,
       CAST(IF(IFNULL(A.dataInicial, 0) = 0, NULL, IFNULL(A.dataInicial, 0)) AS DATE) AS dataInicial,
       A.dataUpdate                                                                   AS dataUpdate,
       A.kardec                                                                       AS kardec,
       A.dataObservacao                                                               AS dataObservacao,
       A.observacao                                                                   AS observacao,
       PC.refprice / 100                                                              AS preco,
       A.estoqueCD                                                                    AS estoqueCD,
       A.estoqueLoja                                                                  AS estoqueLoja,
       B.codbar                                                                       AS barcode,
       P.mfno_ref                                                                     AS ref,
       AC.numero                                                                       AS numeroAcerto,
       AC.processado                                                                   AS processado
FROM
  sqldados.stk                AS E
    INNER JOIN sqldados.store AS S
               ON E.storeno = S.no
    INNER JOIN sqldados.prd   AS P
               ON E.prdno = P.no
    LEFT JOIN  sqldados.vend  AS V
               ON V.no = P.mfno
    LEFT JOIN  T_LOC_APP      AS A
               USING (prdno, grade)
    LEFT JOIN  T_BARCODE      AS B
               USING (prdno, grade)
    LEFT JOIN  sqldados.prp   AS PC
               ON PC.storeno = 10 AND PC.prdno = E.prdno
    LEFT JOIN  T_ACERTO       AS AC
               ON E.storeno = AC.numloja AND E.prdno = AC.prdno AND E.grade = AC.grade
WHERE (((P.dereg & POW(2, 2) = 0) AND (:inativo = 'N')) OR ((P.dereg & POW(2, 2) != 0) AND (:inativo = 'S')) OR
       (:inativo = 'T'))
  AND (P.groupno = :centroLucro OR P.deptno = :centroLucro OR P.clno = :centroLucro OR :centroLucro = 0)
  AND (E.prdno = :prdno OR :prdno = '')
  AND ((:caracter = 'S' AND P.name NOT REGEXP '^[A-Z0-9]') OR (:caracter = 'N' AND P.name REGEXP '^[A-Z0-9]') OR
       (:caracter = 'T'))
  AND (P.mfno = :fornecedor OR V.sname LIKE CONCAT('%', :fornecedor, '%') OR :fornecedor = '')
  AND E.storeno = 4
GROUP BY E.prdno, E.grade
HAVING (:estoque = '>' AND saldo > :saldo)
    OR (:estoque = '<' AND saldo < :saldo)
    OR (:estoque = '=' AND saldo = :saldo)
    OR (:estoque = 'T');


SELECT loja,
       lojaSigla,
       prdno,
       codigo,
       descricao,
       unidade,
       grade,
       embalagem,
       qtdEmbalagem,
       estoque,
       locApp,
       codForn,
       fornecedor,
       saldo,
       dataInicial,
       dataUpdate,
       kardec,
       dataObservacao,
       observacao,
       preco,
       estoqueCD,
       estoqueLoja,
       barcode,
       ref
FROM
  temp_pesquisa
WHERE (@PESQUISA = '' OR codigo = @PESQUISANUM OR descricao LIKE @PESQUISALIKE OR unidade LIKE @PESQUISA)
  AND (grade LIKE CONCAT(:grade, '%') OR :grade = '')
  AND ((locApp LIKE CONCAT(:localizacao, '%') OR :localizacao = '') AND
       (locApp IN (:localizacaoUser) OR 'TODOS' IN (:localizacaoUser) OR IFNULL(locApp, '') = ''))
