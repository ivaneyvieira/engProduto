USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DO @FORNECEDOR_NUMERO := IF(:fornecedor REGEXP '^[0-9]+$', :fornecedor, 0);
DO @FORNECEDOR_NOME := IF(:fornecedor REGEXP '^[0-9]+$', '', :fornecedor);

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT no AS prdno, mfno, mfno_ref, name, typeno, clno, qttyPackClosed
FROM
  sqldados.prd AS P
WHERE (((P.dereg & POW(2, 2) = 0) AND (:inativo = 'N')) OR
       ((P.dereg & POW(2, 2) != 0) AND (:inativo = 'S')) OR
       (:inativo = 'T'))
  AND (P.groupno = :centroLucro OR P.deptno = :centroLucro OR P.clno = :centroLucro OR :centroLucro = 0)
  AND ((:caracter = 'S' AND P.name NOT REGEXP '^[A-Z0-9]') OR (:caracter = 'N' AND P.name REGEXP '^[A-Z0-9]') OR
       (:caracter = 'T'))
  AND (P.no = :prdno OR :prdno = '')
  AND (P.clno = :cl OR P.deptno = :cl OR P.groupno = :cl OR :cl = 0)
  AND CASE :letraDup
        WHEN 'S' THEN (SUBSTRING_INDEX(P.name, ' ', 2) REGEXP
                       '^..(AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ)' OR
                       P.name LIKE '3MM%') = TRUE
        WHEN 'N' THEN (SUBSTRING_INDEX(P.name, ' ', 2) REGEXP
                       '^..(AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ)' OR
                       P.name LIKE '3MM%') = FALSE
        WHEN 'T' THEN TRUE
                 ELSE FALSE
      END;

DROP TEMPORARY TABLE IF EXISTS T_PRDNO;
CREATE TEMPORARY TABLE T_PRDNO
(
  PRIMARY KEY (prdno)
)
SELECT prdno
FROM
  T_PRD;

DROP TEMPORARY TABLE IF EXISTS T_LOC_NERUS;
CREATE TEMPORARY TABLE T_LOC_NERUS
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno       AS prdno,
       grade       AS grade,
       localizacao AS locNerus
FROM
  sqldados.prdloc
    INNER JOIN T_PRDNO
               USING (prdno)
WHERE storeno IN (2, 3, 4, 5, 8)
  AND (storeno = :loja OR :loja = 0)
  AND (prdno = :prdno OR :prdno = '')
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_LOC_APP;
CREATE TEMPORARY TABLE T_LOC_APP
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno       AS prdno,
       grade       AS grade,
       dataInicial AS dataInicial,
       estoqueLoja AS estoqueLoja,
       kardexLoja  AS kardexLoja
FROM
  sqldados.prdControle
    INNER JOIN T_PRDNO
               USING (prdno)
WHERE (storeno IN (2, 3, 4, 5, 8))
  AND (storeno = IF(:loja = 0, 4, :loja))
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
  sqldados.prd                 AS P
    INNER JOIN T_PRDNO
               ON T_PRDNO.prdno = P.no
    LEFT JOIN  sqldados.prd2   AS P2
               ON P.no = P2.prdno
    LEFT JOIN  sqldados.prdbar AS B
               ON P.no = B.prdno AND B.grade != ''
GROUP BY P.no, B.grade
HAVING codbar != '';

DROP TEMPORARY TABLE IF EXISTS temp_pesquisa;
CREATE TEMPORARY TABLE temp_pesquisa
SELECT S.no                                                                           AS loja,
       S.sname                                                                        AS lojaSigla,
       E.prdno                                                                        AS prdno,
       TRIM(PD.prdno) * 1                                                             AS codigo,
       TRIM(MID(PD.name, 1, 37))                                                      AS descricao,
       TRIM(MID(PD.name, 38, 3))                                                      AS unidade,
       PD.typeno                                                                      AS tipo,
       PD.clno                                                                        AS cl,
       E.grade                                                                        AS grade,
       ROUND(PD.qttyPackClosed / 1000)                                                AS embalagem,
       SUM(CASE
             WHEN PD.name LIKE 'SVS E-COLOR%' THEN TRUNCATE(
                 ROUND((E.qtty_atacado + E.qtty_varejo) / 1000) / 900, 2)
             WHEN PD.name LIKE 'VRC COLOR%'   THEN TRUNCATE(
                 ROUND((E.qtty_atacado + E.qtty_varejo) / 1000) / 1000, 2)
                                              ELSE TRUNCATE(
                                                  ROUND((E.qtty_atacado + E.qtty_varejo) / 1000) /
                                                  (PD.qttyPackClosed / 1000), 0)
           END)                                                                       AS qtdEmbalagem,
       LN.locNerus                                                                    AS locNerus,
       V.no                                                                           AS codForn,
       V.name                                                                         AS fornecedor,
       V.sname                                                                        AS fornecedorAbrev,
       V.cgc                                                                          AS cnpjFornecedor,
       ROUND(SUM(E.qtty_atacado + E.qtty_varejo) / 1000)                              AS saldo,
       ROUND(SUM(E.qtty_atacado + E.qtty_varejo) / 1000) * (E.cm_real / 10000)        AS valorEstoque,
       ROUND(SUM(E.qtty_varejo) / 1000)                                               AS saldoVarejo,
       ROUND(SUM(E.qtty_atacado) / 1000)                                              AS saldoAtacado,
       CAST(IF(IFNULL(A.dataInicial, 0) = 0, NULL, IFNULL(A.dataInicial, 0)) AS DATE) AS dataInicial,
       A.estoqueLoja                                                                  AS estoqueLoja,
       A.kardexLoja                                                                   AS kardexLoja,
       PC.refprice / 100                                                              AS preco,
       B.codbar                                                                       AS barcode,
       PD.mfno_ref                                                                    AS ref
FROM
  sqldados.stk                AS E
    INNER JOIN sqldados.store AS S
               ON E.storeno = S.no
    INNER JOIN T_PRD          AS PD
               USING (prdno)
    LEFT JOIN  sqldados.vend  AS V
               ON V.no = PD.mfno
    LEFT JOIN  T_LOC_APP      AS A
               USING (prdno, grade)
    LEFT JOIN  T_LOC_NERUS    AS LN
               USING (prdno, grade)
    LEFT JOIN  T_BARCODE      AS B
               USING (prdno, grade)
    LEFT JOIN  sqldados.prp   AS PC
               ON PC.storeno = 10 AND PC.prdno = E.prdno
WHERE (E.storeno = :loja OR :loja = 0)
  AND (PD.mfno = @FORNECEDOR_NUMERO OR @FORNECEDOR_NUMERO = 0)
  AND (V.name LIKE CONCAT('%', @FORNECEDOR_NOME, '%') OR @FORNECEDOR_NOME = '')
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
       tipo,
       cl,
       embalagem,
       qtdEmbalagem,
       locNerus,
       codForn,
       fornecedor,
       fornecedorAbrev,
       cnpjFornecedor,
       saldo,
       valorEstoque,
       saldoVarejo,
       saldoAtacado,
       dataInicial,
       estoqueLoja,
       kardexLoja,
       barcode,
       ref
FROM
  temp_pesquisa
WHERE (@PESQUISA = '' OR codigo = @PESQUISANUM OR descricao LIKE @PESQUISALIKE OR unidade LIKE @PESQUISA)
  AND (grade LIKE CONCAT(:grade, '%') OR :grade = '')
