USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT A.prdno AS prdno, IF(:grade = 'S', A.grade, '') AS gradeProduto, COALESCE(A.localizacao, '') AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE A.storeno = 4
  AND A.localizacao != ''
GROUP BY A.prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_REL;
CREATE TEMPORARY TABLE T_REL
(
  PRIMARY KEY (prdno, temRelacionado)
)
SELECT prdno AS prdnoRel, prdno_rel AS prdno, 'S' AS temRelacionado
FROM
  sqldados.prdrel
GROUP BY prdno_rel;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT P.no                                    AS prdno,
       TRIM(P.no) * 1                          AS codigo,
       TRIM(MID(P.name, 1, 37))                AS descricao,
       TRIM(MID(P.name, 38, 3))                AS unidade,
       P.taxno                                 AS tributacao,
       IFNULL(R.form_label, '')                AS rotulo,
       N.ncm                                   AS ncm,
       P.mfno                                  AS fornecedor,
       V.sname                                 AS abrev,
       P.typeno                                AS tipo,
       P.clno                                  AS cl,
       CASE tipoGarantia
         WHEN 0 THEN 'Dias'
         WHEN 1 THEN 'Semanas'
         WHEN 2 THEN 'Meses'
         WHEN 3 THEN 'Anos'
                ELSE ''
       END                                     AS tipoValidade,
       IF(tipoGarantia = 2, garantia, NULL)    AS mesesGarantia,
       IF((P.bits % POW(2, 10)) > 0, 'S', 'N') AS temRelacionado
FROM
  sqldados.prd                 AS P
    LEFT JOIN sqldados.prdalq  AS R
              ON R.prdno = P.no
    LEFT JOIN sqldados.spedprd AS N
              ON N.prdno = P.no
    LEFT JOIN sqldados.vend    AS V
              ON V.no = P.mfno
WHERE (P.mfno = :fornecedor OR :fornecedor = 0)
  AND (P.taxno = :tributacao OR :tributacao = '')
  AND (R.form_label = :rotulo OR :rotulo = '')
  AND (P.typeno = :tipo OR :tipo = 0)
  AND (P.clno = :cl OR P.deptno = :cl OR P.groupno = :cl OR :cl = 0)
  AND CASE :caracter
        WHEN 'S' THEN P.name NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN P.name REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
                 ELSE FALSE
      END
  AND CASE :letraDup
        WHEN 'S' THEN SUBSTRING_INDEX(P.name, ' ', 1) REGEXP
                      'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ'
        WHEN 'N' THEN SUBSTRING_INDEX(P.name, ' ', 1) NOT REGEXP
                      'AA|BB|CC|DD|EE|FF|GG|HH|II|JJ|KK|LL|MM|NN|OO|PP|QQ|RR|SS|TT|UU|VV|WW|XX|YY|ZZ'
        WHEN 'T' THEN TRUE
                 ELSE FALSE
      END
  AND (:consumo = 'T' OR (:consumo = 'S' AND P.no * 1 >= 900000) OR (:consumo = 'N' AND P.no * 1 < 900000))
  AND (P.no = LPAD(TRIM(:produto), 16, ' ') OR P.name LIKE CONCAT(TRIM(:produto), '%') OR TRIM(:produto) = '');

DROP TEMPORARY TABLE IF EXISTS T_CUST;
CREATE TEMPORARY TABLE T_CUST
(
  PRIMARY KEY (custno)
)
SELECT C.no AS custno
FROM
  sqldados.custp              AS C
    INNER JOIN sqldados.store AS S
               ON C.cpf_cgc = S.cgc
WHERE S.no IN (2, 3, 4, 5, 8);

DROP TEMPORARY TABLE IF EXISTS T_SALDO_SAIDA;
CREATE TEMPORARY TABLE T_SALDO_SAIDA
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT X.prdno                     AS prdno,
       IF(:grade = 'S', grade, '') AS gradeProduto,
       SUM(X.qtty / 1000)          AS quant,
       SUM(X.precoUnitario / 100)  AS valorTotal
FROM
  sqldados.nf                  AS N
    INNER JOIN sqldados.xaprd2 AS X
               USING (storeno, pdvno, xano)
    INNER JOIN T_CUST          AS C
               USING (custno)
WHERE N.cfo = 5927
  AND N.storeno IN (2, 3, 4, 5, 8)
  AND N.remarks = '2'
GROUP BY X.prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_VEND;
CREATE TEMPORARY TABLE T_VEND
(
  PRIMARY KEY (vendno)
)
SELECT V.no AS vendno
FROM
  sqldados.vend               AS V
    INNER JOIN sqldados.store AS S
               ON V.cgc = S.cgc
WHERE S.no IN (2, 3, 4, 5, 8);

DROP TEMPORARY TABLE IF EXISTS T_SALDO_ENTRADA;
CREATE TEMPORARY TABLE T_SALDO_ENTRADA
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT prdno                                AS prdno,
       IF(:grade = 'S', grade, '')          AS gradeProduto,
       SUM(X.qtty / 1000)                   AS quant,
       SUM((X.fob / 100) * (X.qtty / 1000)) AS valorTotal
FROM
  sqldados.inv               AS N
    INNER JOIN T_VEND        AS V
               USING (vendno)
    INNER JOIN sqldados.iprd AS X
               USING (invno)
WHERE N.cfo = 1202
  AND N.storeno IN (2, 3, 4, 5, 8)
  AND N.remarks = '2'
GROUP BY X.prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_STKLOJA;
CREATE TEMPORARY TABLE T_STKLOJA
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT prdno                                                                   AS prdno,
       IF(:grade = 'S', grade, '')                                             AS gradeProduto,
       SUM(IF(storeno = 2, qtty_atacado / 1000, 0.00))                         AS estoqueDSAtacado,
       SUM(IF(storeno = 3, qtty_atacado / 1000, 0.00))                         AS estoqueMRAtacado,
       SUM(IF(storeno = 4, qtty_atacado / 1000, 0.00))                         AS estoqueMFAtacado,
       SUM(IF(storeno = 5, qtty_atacado / 1000, 0.00))                         AS estoquePKAtacado,
       SUM(IF(storeno = 8, qtty_atacado / 1000, 0.00))                         AS estoqueTMAtacado,
       SUM(IF(storeno = 2, (qtty_atacado / 1000) * (cm_varejo / 10000), 0.00)) AS custoDSAtacado,
       SUM(IF(storeno = 3, (qtty_atacado / 1000) * (cm_varejo / 10000), 0.00)) AS custoMRAtacado,
       SUM(IF(storeno = 4, (qtty_atacado / 1000) * (cm_varejo / 10000), 0.00)) AS custoMFAtacado,
       SUM(IF(storeno = 5, (qtty_atacado / 1000) * (cm_varejo / 10000), 0.00)) AS custoPKAtacado,
       SUM(IF(storeno = 8, (qtty_atacado / 1000) * (cm_varejo / 10000), 0.00)) AS custoTMAtacado,
       SUM(qtty_atacado / 1000)                                                AS estoqueLojasAtacado,
       SUM((qtty_atacado / 1000) * (cm_varejo / 10000))                        AS custoLojasAtacado
FROM
  sqldados.stk
    INNER JOIN T_PRD
               USING (prdno)
WHERE storeno IN (2, 3, 4, 5, 8)
GROUP BY prdno, gradeProduto;

DROP TEMPORARY TABLE IF EXISTS T_PRDSTK;
CREATE TEMPORARY TABLE T_PRDSTK
(
  PRIMARY KEY (prdno, gradeProduto)
)
SELECT S.prdno                                             AS prdno,
       P.codigo * 1                                        AS codigo,
       P.descricao                                         AS descricao,
       S.gradeProduto                                      AS gradeProduto,
       P.unidade                                           AS unidade,
       estoqueLojasAtacado                                 AS estoqueLojasAtacado,
       custoLojasAtacado                                   AS custoLojasAtacado,
       estoqueDSAtacado                                    AS estoqueDSAtacado,
       estoqueMRAtacado                                    AS estoqueMRAtacado,
       estoqueMFAtacado                                    AS estoqueMFAtacado,
       estoquePKAtacado                                    AS estoquePKAtacado,
       estoqueTMAtacado                                    AS estoqueTMAtacado,
       custoDSAtacado                                      AS custoDSAtacado,
       custoMRAtacado                                      AS custoMRAtacado,
       custoMFAtacado                                      AS custoMFAtacado,
       custoPKAtacado                                      AS custoPKAtacado,
       custoTMAtacado                                      AS custoTMAtacado,
       P.tributacao                                        AS tributacao,
       P.rotulo                                            AS rotulo,
       P.ncm                                               AS ncm,
       P.fornecedor                                        AS fornecedor,
       P.abrev                                             AS abrev,
       P.tipo                                              AS tipo,
       P.cl                                                AS cl,
       tipoValidade                                        AS tipoValidade,
       mesesGarantia                                       AS mesesGarantia,
       MID(L.localizacao, 1, 4)                            AS localizacao,
       R.prdnoRel                                          AS prdnoRel,
       TRIM(R.prdnoRel) * 1                                AS codigoRel,
       IFNULL(SE.quant, 0) - IFNULL(SS.quant, 0)           AS quantSaldoSaida,
       IFNULL(SE.valorTotal, 0) - IFNULL(SS.valorTotal, 0) AS valorTotalSaldoSaida
FROM
  T_PRD                        AS P
    INNER JOIN T_STKLOJA       AS S
               USING (prdno)
    LEFT JOIN  T_LOC           AS L
               USING (prdno, gradeProduto)
    LEFT JOIN  T_REL           AS R
               USING (prdno, temRelacionado)
    LEFT JOIN  T_SALDO_SAIDA   AS SS
               USING (prdno, gradeProduto)
    LEFT JOIN  T_SALDO_ENTRADA AS SE
               USING (prdno, gradeProduto)
WHERE ((:estoque = '<' AND S.estoqueLojasAtacado < :saldo) OR
       (:estoque = '>' AND S.estoqueLojasAtacado > :saldo) OR
       (:estoque = '=' AND S.estoqueLojasAtacado = :saldo) OR
       (:estoque = 'T'));

SELECT prdno,
       codigo,
       descricao,
       gradeProduto,
       unidade,
       tipoValidade,
       mesesGarantia,
       estoqueLojasAtacado,
       custoLojasAtacado,
       estoqueDSAtacado,
       estoqueMRAtacado,
       estoqueMFAtacado,
       estoquePKAtacado,
       estoqueTMAtacado,
       custoDSAtacado,
       custoMRAtacado,
       custoMFAtacado,
       custoPKAtacado,
       custoTMAtacado,
       tributacao,
       rotulo,
       ncm,
       fornecedor,
       abrev,
       tipo,
       cl,
       localizacao,
       prdnoRel,
       codigoRel,
       quantSaldoSaida,
       valorTotalSaldoSaida
FROM
  T_PRDSTK AS S
WHERE (@PESQUISA = '' OR codigo = @PESQUISA OR descricao LIKE @PESQUISA_LIKE OR gradeProduto LIKE @PESQUISA_START OR
       unidade = @PESQUISA OR tributacao = @PESQUISA OR rotulo LIKE @PESQUISA_START OR ncm LIKE @PESQUISA_START OR
       fornecedor = @PESQUISA OR abrev LIKE @PESQUISA_LIKE OR tipoValidade LIKE @PESQUISA_LIKE OR cl = @PESQUISA)