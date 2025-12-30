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
  AND (((P.bits & POW(2, 13) = 0) AND (:uso = 'N')) OR
       ((P.bits & POW(2, 13) != 0) AND (:uso = 'S')) OR
       (:uso = 'T'))
  AND (P.groupno = :centroLucro OR P.deptno = :centroLucro OR P.clno = :centroLucro OR :centroLucro = 0)
  AND ((:caracter = 'S' AND P.name NOT REGEXP '^[A-Z0-9]') OR (:caracter = 'N' AND P.name REGEXP '^[A-Z0-9]') OR
       (:caracter = 'T'))
  AND (P.no = :prdno OR :prdno = '')
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

DO @MES_ATUAL := MID(CURDATE() * 1, 1, 6) * 1;
DO @NES_ANTERIOR := MID(SUBDATE(CURDATE(), INTERVAL 1 MONTH) * 1, 1, 6) * 1;

DROP TEMPORARY TABLE IF EXISTS T_PRD_DEV;
CREATE TEMPORARY TABLE T_PRD_DEV
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT N.storeno           AS storeno,
       prdno               AS prdno,
       grade               AS grade,
       SUM(quantDevolucao) AS quantDevolucao
FROM
  sqldados.iprdAdicionalDev          AS A
    LEFT JOIN  sqldados.invAdicional AS IA
               USING (invno, tipoDevolucao, numero)
    INNER JOIN T_PRD                 AS P
               USING (prdno)
    INNER JOIN sqldados.inv          AS N
               USING (invno)
WHERE (situacaoDev = 0 OR situacaoDev IS NULL)
  AND tipoDevolucao = 8
GROUP BY storeno, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_PRD_VENDA;
CREATE TEMPORARY TABLE T_PRD_VENDA
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno                                       AS prdno,
       grade                                       AS grade,
       SUM(IF(ym = @MES_ATUAL, ROUND(qtty), 0))    AS vendaMesAtual,
       SUM(IF(ym = @NES_ANTERIOR, ROUND(qtty), 0)) AS vendaMesAnterior
FROM
  sqldados.smlp      AS S
    INNER JOIN T_PRD AS P
               USING (prdno)
WHERE ym IN (@MES_ATUAL, @NES_ANTERIOR)
  AND S.storeno IN (2, 3, 4, 5, 8)
  AND (S.storeno = :loja OR :loja = 0)
GROUP BY prdno, grade;

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
WHERE storeno IN (2, 3, 4, 5, 8)
  AND (storeno = :loja OR :loja = 0)
  AND (prdno = :prdno OR :prdno = '')
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_LOC_APP;
CREATE TEMPORARY TABLE T_LOC_APP
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno                                                     AS prdno,
       grade                                                     AS grade,
       localizacao                                               AS locApp,
       dataInicial                                               AS dataInicial,
       IF(dataUpdate * 1 = 0, NULL, dataUpdate)                  AS dataUpdate,
       kardec                                                    AS kardec,
       IF(dataObservacao * 1 = 0, CURDATE() * 1, dataObservacao) AS dataObservacao,
       qtConferencia                                             AS qtConferencia,
       qtConfEdit                                                AS qtConfEdit,
       qtConfEditLoja                                            AS qtConfEditLoja,
       estoqueConfCD                                             AS estoqueConfCD,
       estoqueConfLoja                                           AS estoqueConfLoja,
       estoque                                                   AS estoque,
       estoqueData                                               AS estoqueData,
       estoqueCD                                                 AS estoqueCD,
       estoqueLoja                                               AS estoqueLoja,
       estoqueUser                                               AS estoqueUser
FROM
  sqldados.prdAdicional
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
SELECT A.numero,
       A.numloja,
       A.data,
       A.hora,
       A.usuario,
       A.prdno,
       A.grade,
       A.estoqueSis,
       A.estoqueLoja,
       A.estoqueCD,
       A.processado,
       A.transacao,
       A.login
FROM
  sqldados.produtoEstoqueAcerto A
    INNER JOIN T_ULT_ACERTO
               USING (numloja, prdno, grade, numero);

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
       IFNULL(A.estoque, 0)                                                           AS estoque,
       LN.locNerus                                                                    AS locNerus,
       IFNULL(A.locApp, '')                                                           AS locApp,
       V.no                                                                           AS codForn,
       V.name                                                                         AS fornecedor,
       V.sname                                                                        AS fornecedorAbrev,
       V.cgc                                                                          AS cnpjFornecedor,
       ROUND(SUM(E.qtty_atacado + E.qtty_varejo) / 1000)                              AS saldo,
       ROUND(SUM(E.qtty_atacado + E.qtty_varejo) / 1000) * (E.cm_real / 10000)        AS valorEstoque,
       ROUND(SUM(E.qtty_varejo) / 1000)                                               AS saldoVarejo,
       ROUND(SUM(E.qtty_atacado) / 1000)                                              AS saldoAtacado,
       CAST(IF(IFNULL(A.dataInicial, 0) = 0, NULL, IFNULL(A.dataInicial, 0)) AS DATE) AS dataInicial,
       A.dataUpdate                                                                   AS dataUpdate,
       A.kardec                                                                       AS kardec,
       A.estoqueConfCD                                                                AS estoqueConfCD,
       A.estoqueConfLoja                                                              AS estoqueConfLoja,
       CAST(A.dataObservacao AS DATE)                                                 AS dataConferencia,
       qtConferencia                                                                  AS qtConferencia,
       qtConfEdit                                                                     AS qtConfEdit,
       qtConfEditLoja                                                                 AS qtConfEditLoja,
       PC.refprice / 100                                                              AS preco,
       A.estoqueUser                                                                  AS estoqueUser,
       U.login                                                                        AS estoqueLogin,
       A.estoqueData                                                                  AS estoqueData,
       AC.estoqueCD                                                                   AS estoqueCD,
       AC.estoqueLoja                                                                 AS estoqueLoja,
       B.codbar                                                                       AS barcode,
       PD.mfno_ref                                                                    AS ref,
       AC.numero                                                                      AS numeroAcerto,
       AC.processado                                                                  AS processado,
       SV.vendaMesAnterior                                                            AS vendaMesAnterior,
       SV.vendaMesAtual                                                               AS vendaMesAtual,
       DEV.quantDevolucao                                                             AS quantDevolucao
FROM
  sqldados.stk                AS E
    INNER JOIN sqldados.store AS S
               ON E.storeno = S.no
    INNER JOIN T_PRD          AS PD
               USING (prdno)
    LEFT JOIN  T_PRD_DEV      AS DEV
               USING (storeno, prdno, grade)
    LEFT JOIN  sqldados.vend  AS V
               ON V.no = PD.mfno
    LEFT JOIN  T_LOC_APP      AS A
               USING (prdno, grade)
    LEFT JOIN  T_LOC_NERUS    AS LN
               USING (prdno, grade)
    LEFT JOIN  T_PRD_VENDA    AS SV
               USING (prdno, grade)
    LEFT JOIN  sqldados.users AS U
               ON U.no = A.estoqueUser
    LEFT JOIN  T_BARCODE      AS B
               USING (prdno, grade)
    LEFT JOIN  sqldados.prp   AS PC
               ON PC.storeno = 10 AND PC.prdno = E.prdno
    LEFT JOIN  T_ACERTO       AS AC
               ON E.storeno = AC.numloja AND E.prdno = AC.prdno AND E.grade = AC.grade
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
       estoque,
       locNerus,
       locApp,
       codForn,
       fornecedor,
       fornecedorAbrev,
       cnpjFornecedor,
       saldo,
       valorEstoque,
       saldoVarejo,
       saldoAtacado,
       dataInicial,
       dataUpdate,
       kardec,
       IFNULL(dataConferencia, CURDATE()) AS dataConferencia,
       qtConferencia,
       qtConfEdit,
       qtConfEditLoja,
       preco,
       estoqueUser,
       estoqueLogin,
       estoqueData,
       estoqueCD,
       estoqueLoja,
       barcode,
       ref,
       numeroAcerto,
       processado,
       estoqueConfCD,
       estoqueConfLoja,
       vendaMesAnterior,
       vendaMesAtual,
       quantDevolucao
FROM
  temp_pesquisa
WHERE (@PESQUISA = '' OR codigo = @PESQUISANUM OR descricao LIKE @PESQUISALIKE OR unidade LIKE @PESQUISA OR
       (DATE_FORMAT(estoqueData, '%d/%m/%Y') LIKE @PESQUISALIKE))
  AND (grade LIKE CONCAT(:grade, '%') OR :grade = '')
  AND (locApp LIKE CONCAT(:localizacao, '%') OR :localizacao = '')
  AND (MID(locApp, 1, 4) IN (:localizacaoUser) OR 'TODOS' IN (:localizacaoUser) OR TRIM(IFNULL(locApp, '')) = '')
  AND (numeroAcerto = :pedido OR :pedido = 0)
