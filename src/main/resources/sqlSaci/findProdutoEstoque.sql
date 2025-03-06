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
       GROUP_CONCAT(DISTINCT localizacao ORDER BY 1) AS locApp,
       MAX(dataInicial)                              AS dataInicial,
       MAX(dataUpdate)                               AS dataUpdate,
       MAX(kardec)                                   AS kardec,
       MAX(estoque)                                  AS estoque
FROM
  sqldados.prdAdicional
WHERE localizacao <> ''
  AND storeno = 4
  AND (prdno = :prdno OR :prdno = '')
GROUP BY prdno, grade;

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
       L1.locSaci                                                                     AS locSaci,
       A.locApp                                                                       AS locApp,
       V.no                                                                           AS codForn,
       V.sname                                                                        AS fornecedor,
       ROUND(SUM(IF(E.storeno = 4, E.qtty_atacado + E.qtty_varejo, 0)) / 1000)        AS saldo,
       CAST(IF(IFNULL(A.dataInicial, 0) = 0, NULL, IFNULL(A.dataInicial, 0)) AS DATE) AS dataInicial,
       A.dataUpdate                                                                   AS dataUpdate,
       A.kardec                                                                       AS kardec
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
    LEFT JOIN  T_LOC_SACI     AS L1
               USING (prdno, grade)
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
       locSaci,
       locApp,
       codForn,
       fornecedor,
       saldo,
       dataInicial,
       dataUpdate,
       kardec
FROM
  temp_pesquisa
WHERE (@PESQUISA = '' OR locSaci LIKE @PESQUISALIKE OR codigo = @PESQUISANUM OR locSaci LIKE @PESQUISALIKE OR
       descricao LIKE @PESQUISALIKE OR unidade LIKE @PESQUISA)
  AND (grade LIKE CONCAT(:grade, '%') OR :grade = '')
  AND (((locApp LIKE CONCAT(:localizacao, '%') OR :localizacao = '') AND
        (locApp IN (:localizacaoUser) OR 'TODOS' IN (:localizacaoUser))) OR IFNULL(locApp, '') = '')




