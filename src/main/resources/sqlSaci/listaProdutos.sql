USE sqldados;

DO @CODIGO := :codigo;
DO @PRDNO := LPAD(@CODIGO, 16, ' ');
DO @LISTVEND := REPLACE(:listVend, ' ', '');
DO @TRIBUTACAO := :tributacao;
DO @TYPENO := :typeno;
DO @CLNO := :clno;
DO @DIVENDA := :diVenda;
DO @DFVENDA := IF(:dfVenda = 0, 99999999, :dfVenda);
DO @DICOMPRA := :diCompra;
DO @DFCOMPRA := IF(:dfCompra = 0, 99999999, :dfCompra);
DO @TEMGRADE := :temGrade;
DO @GRADE := :grade;
DO @LOJA := :loja;

DO @PEDIDO := IF(:pesquisa REGEXP '^[0-9]{1,2} [0-9]+(-[0-9]+)?$', :pesquisa, '');
DO @PEDIDO_LOJA := SUBSTRING_INDEX(@PEDIDO, ' ', 1) * 1;
DO @PEDIDO_NUMEROS := SUBSTRING_INDEX(@PEDIDO, ' ', -1);
DO @PEDIDO_I := SUBSTRING_INDEX(@PEDIDO_NUMEROS, '-', 1) * 1;
DO @PEDIDO_F := SUBSTRING_INDEX(@PEDIDO_NUMEROS, '-', -1) * 1;

DO @PESQUISA := IF(:pesquisa NOT REGEXP '^[0-9]{1,2} [0-9]+(-[0-9]+)?$', :pesquisa, '');
DO @PESQUISA_LIKE := CONCAT(@PESQUISA, '%');

DROP TABLE IF EXISTS T_ORDS;
CREATE TEMPORARY TABLE T_ORDS
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM(qtty) AS qtPedido
FROM sqldados.oprd
WHERE storeno = @PEDIDO_LOJA
  AND ordno BETWEEN @PEDIDO_I AND @PEDIDO_F
GROUP BY prdno, grade;

DROP TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT no prdno,
       mfno,
       dereg,
       name,
       taxno,
       typeno,
       clno,
       qttyPackClosed,
       garantia,
       tipoGarantia,
       barcode,
       mfno_ref,
       weight_g
FROM sqldados.prd AS P
WHERE (P.no = @PRDNO OR @CODIGO = 0)
  AND (FIND_IN_SET(P.mfno, @LISTVEND) OR @LISTVEND = '')
  AND (P.typeno = @TYPENO OR @TYPENO = 0)
  AND (P.clno = @CLNO OR P.deptno = @CLNO OR P.groupno = @CLNO OR @CLNO = 0)
  AND (P.taxno = @TRIBUTACAO OR @TRIBUTACAO = '')
  AND CASE :marca
        WHEN 'T' THEN TRUE
        WHEN 'N' THEN MID(P.name, 1, 1) NOT IN ('.', '*', '!', '*', ']', ':', '#')
        WHEN 'S' THEN MID(P.name, 1, 1) IN ('.', '*', '!', '*', ']', ':', '#')
        ELSE FALSE
      END
  AND CASE :inativo
        WHEN 'T' THEN TRUE
        WHEN 'S' THEN (P.dereg & POW(2, 2)) != 0
        WHEN 'N' THEN (P.dereg & POW(2, 2)) = 0
        ELSE FALSE
      END
  AND (IF(tipoGarantia = 2, garantia, 0) = :validade OR :validade = 0)
  AND IF(:temValidade, IF(tipoGarantia = 2, garantia, 0) > 0, TRUE);

DROP TABLE IF EXISTS T_STK_JS;
CREATE TEMPORARY TABLE T_STK_JS
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT prdno,
       IF(@TEMGRADE = 'S', grade, '')         AS gradeOpt,
       SUM(qtty_varejo + qtty_atacado) / 1000 AS estoque
FROM sqldados.stk AS S
       INNER JOIN T_PRD
                  USING (prdno)
WHERE S.storeno = 1
GROUP BY prdno, gradeOpt
HAVING estoque != 0;

DROP TABLE IF EXISTS T_STK_DS;
CREATE TEMPORARY TABLE T_STK_DS
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT prdno,
       IF(@TEMGRADE = 'S', grade, '')         AS gradeOpt,
       SUM(qtty_varejo + qtty_atacado) / 1000 AS estoque
FROM sqldados.stk AS S
       INNER JOIN T_PRD
                  USING (prdno)
WHERE S.storeno = 2
GROUP BY prdno, gradeOpt
HAVING estoque != 0;

DROP TABLE IF EXISTS T_STK_MR;
CREATE TEMPORARY TABLE T_STK_MR
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT prdno,
       IF(@TEMGRADE = 'S', grade, '')         AS gradeOpt,
       SUM(qtty_varejo + qtty_atacado) / 1000 AS estoque
FROM sqldados.stk AS S
       INNER JOIN T_PRD
                  USING (prdno)
WHERE S.storeno = 3
GROUP BY prdno, gradeOpt
HAVING estoque != 0;

DROP TABLE IF EXISTS T_STK_MF;
CREATE TEMPORARY TABLE T_STK_MF
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT prdno,
       IF(@TEMGRADE = 'S', grade, '')         AS gradeOpt,
       SUM(qtty_varejo + qtty_atacado) / 1000 AS estoque
FROM sqldados.stk AS S
       INNER JOIN T_PRD
                  USING (prdno)
WHERE S.storeno = 4
GROUP BY prdno, gradeOpt
HAVING estoque != 0;

DROP TABLE IF EXISTS T_STK_PK;
CREATE TEMPORARY TABLE T_STK_PK
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT prdno,
       IF(@TEMGRADE = 'S', grade, '')         AS gradeOpt,
       SUM(qtty_varejo + qtty_atacado) / 1000 AS estoque
FROM sqldados.stk AS S
       INNER JOIN T_PRD
                  USING (prdno)
WHERE S.storeno = 5
  AND qtty_varejo != qtty_atacado
GROUP BY prdno, gradeOpt
HAVING estoque != 0;

DROP TABLE IF EXISTS T_STK_TM;
CREATE TEMPORARY TABLE T_STK_TM
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT prdno,
       IF(@TEMGRADE = 'S', grade, '')         AS gradeOpt,
       SUM(qtty_varejo + qtty_atacado) / 1000 AS estoque
FROM sqldados.stk AS S
       INNER JOIN T_PRD
                  USING (prdno)
WHERE S.storeno = 8
  AND qtty_varejo != qtty_atacado
GROUP BY prdno, gradeOpt
HAVING estoque != 0;


DROP TABLE IF EXISTS T_BAR;
CREATE TEMPORARY TABLE T_BAR
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT P.prdno                                    AS prdno,
       IFNULL(IF(@TEMGRADE = 'S', grade, ''), '') AS gradeOpt,
       TRIM(IFNULL(B.barcode, P.barcode))         AS barcode
FROM T_PRD AS P
       LEFT JOIN sqldados.prdbar AS B
                 ON P.prdno = B.prdno
GROUP BY prdno, gradeOpt;

DROP TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, gradeOpt)
)
SELECT prdno, IF(@TEMGRADE = 'S', grade, '') AS gradeOpt, MAX(localizacao) AS localizacao
FROM sqldados.prdloc
WHERE storeno = 4
GROUP BY prdno, gradeOpt;


DROP TABLE IF EXISTS T_RESULT;
CREATE TEMPORARY TABLE T_RESULT
(
  PRIMARY KEY (prdno, grade)
)
SELECT B.prdno                                                             AS prdno,
       TRIM(P.prdno) * 1                                                   AS codigo,
       TRIM(MID(P.name, 1, 37))                                            AS descricao,
       IFNULL(B.gradeOpt, '')                                              AS grade,
       CAST(P.mfno AS CHAR ASCII)                                          AS fornStr,
       P.mfno                                                              AS forn,
       P.taxno                                                             AS tributacao,
       V.sname                                                             AS abrev,
       CAST(P.typeno AS CHAR ASCII)                                        AS tipoStr,
       P.typeno                                                            AS tipo,
       P.clno                                                              AS cl,
       RPAD(MID(LPAD(P.clno, 6, '0'), 1, 2), 6, '0') * 1                   AS groupno,
       RPAD(MID(LPAD(P.clno, 6, '0'), 1, 4), 6, '0') * 1                   AS deptno,
       TRIM(IF(B.gradeOpt IS NULL, IFNULL(P2.gtin, P.barcode), B.barcode)) AS codBar,
       ROUND(IFNULL(JS.estoque, 0))                                        AS JS_TT,
       ROUND(IFNULL(DS.estoque, 0))                                        AS DS_TT,
       ROUND(IFNULL(MR.estoque, 0))                                        AS MR_TT,
       ROUND(IFNULL(MF.estoque, 0))                                        AS MF_TT,
       ROUND(IFNULL(PK.estoque, 0))                                        AS PK_TT,
       ROUND(IFNULL(TM.estoque, 0))                                        AS TM_TT,
       ROUND(IFNULL(JS.estoque, 0)) +
       ROUND(IFNULL(DS.estoque, 0)) +
       ROUND(IFNULL(MR.estoque, 0)) +
       ROUND(IFNULL(MF.estoque, 0)) +
       ROUND(IFNULL(PK.estoque, 0)) +
       ROUND(IFNULL(TM.estoque, 0))                                        AS estoque,
       P.taxno                                                             AS trib,
       P.mfno_ref                                                          AS refForn,
       P.weight_g                                                          AS pesoBruto,
       CASE P.tipoGarantia
         WHEN 0 THEN 'Dia'
         WHEN 1 THEN 'Semana'
         WHEN 2 THEN 'Mês'
         WHEN 3 THEN 'Ano'
         ELSE ''
       END                                                                 AS uGar,
       IF(tipoGarantia = 2, garantia, NULL)                                AS mesesGarantia,
       P.garantia                                                          AS tGar,
       P.qttyPackClosed / 1000                                             AS emb,
       IFNULL(N.ncm, '')                                                   AS ncm,
       ''                                                                  AS site,
       TRIM(MID(P.name, 37, 3))                                            AS unidade,
       IF((dereg & POW(2, 2)) = 0, 'N', 'S')                               AS foraLinha,
       MID(L.localizacao, 1, 4)                                            AS localizacao,
       R.form_label                                                        AS rotulo
FROM T_BAR AS B
       LEFT JOIN T_STK_JS AS JS
                 USING (prdno, gradeOpt)
       LEFT JOIN T_STK_DS AS DS
                 USING (prdno, gradeOpt)
       LEFT JOIN T_STK_MR AS MR
                 USING (prdno, gradeOpt)
       LEFT JOIN T_STK_MF AS MF
                 USING (prdno, gradeOpt)
       LEFT JOIN T_STK_PK AS PK
                 USING (prdno, gradeOpt)
       LEFT JOIN T_STK_TM AS TM
                 USING (prdno, gradeOpt)
       INNER JOIN T_PRD AS P
                  USING (prdno)
       LEFT JOIN sqldados.prdalq AS R
                 USING (prdno)
       LEFT JOIN sqldados.prd2 AS P2
                 ON P.prdno = P2.prdno
       LEFT JOIN sqldados.vend AS V
                 ON V.no = P.mfno
       LEFT JOIN sqldados.spedprd AS N
                 ON N.prdno = P.prdno
       LEFT JOIN T_LOC AS L
                 ON L.prdno = B.prdno AND B.gradeOpt = L.gradeOpt
WHERE CASE :estoque
        WHEN 'T' THEN TRUE
        WHEN '=' THEN (ROUND(IFNULL(JS.estoque, 0)) +
                       ROUND(IFNULL(DS.estoque, 0)) +
                       ROUND(IFNULL(MR.estoque, 0)) +
                       ROUND(IFNULL(MF.estoque, 0)) +
                       ROUND(IFNULL(PK.estoque, 0)) +
                       ROUND(IFNULL(TM.estoque, 0))) = :saldo
        WHEN '>' THEN (ROUND(IFNULL(JS.estoque, 0)) +
                       ROUND(IFNULL(DS.estoque, 0)) +
                       ROUND(IFNULL(MR.estoque, 0)) +
                       ROUND(IFNULL(MF.estoque, 0)) +
                       ROUND(IFNULL(PK.estoque, 0)) +
                       ROUND(IFNULL(TM.estoque, 0))) > :saldo
        WHEN '<' THEN (ROUND(IFNULL(JS.estoque, 0)) +
                       ROUND(IFNULL(DS.estoque, 0)) +
                       ROUND(IFNULL(MR.estoque, 0)) +
                       ROUND(IFNULL(MF.estoque, 0)) +
                       ROUND(IFNULL(PK.estoque, 0)) +
                       ROUND(IFNULL(TM.estoque, 0))) < -:saldo
        ELSE FALSE
      END;

DROP TABLE IF EXISTS T_PRDVENDA;
CREATE TEMPORARY TABLE T_PRDVENDA
(
  PRIMARY KEY (prdno, grade)
)
SELECT R.prdno, IF(@TEMGRADE = 'S', X.grade, '') AS grade, MAX(date) AS date, SUM(quant / 1000) AS qtty
FROM T_PRD AS R
       INNER JOIN sqldados.vendaDate AS X ON X.prdno = R.prdno
WHERE (X.storeno IN (1, 2, 3, 4, 5, 6, 8))
  AND (X.storeno = @LOJA OR @LOJA = 0)
  AND date BETWEEN @DIVENDA AND @DFVENDA
GROUP BY R.prdno, IF(@TEMGRADE = 'S', X.grade, '');

DROP TABLE IF EXISTS T_PRDCOMPRA;
CREATE TEMPORARY TABLE T_PRDCOMPRA
(
  PRIMARY KEY (prdno, grade)
)
SELECT R.prdno, IF(@TEMGRADE = 'S', P.grade, '') AS grade, MAX(date) AS date, SUM(quant / 1000) AS qtty
FROM T_PRD AS R
       INNER JOIN sqldados.compraDate P ON P.prdno = R.prdno
WHERE (storeno IN (1, 2, 3, 4, 5, 6, 8))
  AND (storeno = @LOJA OR @LOJA = 0)
  AND date BETWEEN @DICOMPRA AND @DFCOMPRA
GROUP BY R.prdno, IF(@TEMGRADE = 'S', P.grade, '');

DROP TABLE IF EXISTS T_PRDVENCIMENTO;
CREATE TEMPORARY TABLE T_PRDVENCIMENTO
(
  INDEX (prdno, grade)
)
SELECT A.prdno                                                                 AS prdno,
       IF(@TEMGRADE = 'S', grade, '')                                          AS grade,
       V.mesesFabricacao                                                       AS mesesFabricacao,
       I.qtty / 1000                                                           AS entrada,
       CONCAT(N.nfname, '/', N.invse)                                          AS nfEntrada,
       DATE(N.date)                                                            AS dataEntrada,
       SUBDATE(A.vencimento, INTERVAL IF(tipoGarantia = 2, garantia, 0) MONTH) AS fabricacao,
       A.vencimento                                                            AS vencimento
FROM sqldados.iprdAdicional AS A
       INNER JOIN T_PRD AS P
                  USING (prdno)
       INNER JOIN sqldados.iprd AS I
                  USING (invno, prdno, grade)
       INNER JOIN sqldados.inv AS N
                  USING (invno)
       LEFT JOIN sqldados.validadeAdicional AS V
                 ON V.validade = IF(tipoGarantia = 2, garantia, 0)
WHERE A.vencimento IS NOT NULL
  AND IF(tipoGarantia = 2, garantia, 0) > 0
  AND (:temValidade = TRUE)
  AND N.date BETWEEN @DICOMPRA AND @DFCOMPRA
GROUP BY prdno, IF(@TEMGRADE = 'S', grade, '');

SELECT R.prdno,
       codigo,
       descricao,
       R.grade,
       forn,
       abrev,
       tipo,
       cl,
       codBar,
       tributacao,
       DS_TT,
       MR_TT,
       MF_TT,
       PK_TT,
       TM_TT,
       estoque,
       trib,
       refForn,
       pesoBruto,
       uGar,
       tGar,
       emb,
       ncm,
       site,
       unidade,
       foraLinha,
       mesesGarantia,
       CAST(V.date AS DATE)     AS ultVenda,
       CAST(C.date AS DATE)     AS ultCompra,
       ROUND(IFNULL(V.qtty, 0)) AS qttyVendas,
       ROUND(IFNULL(C.qtty, 0)) AS qttyCompra,
       localizacao,
       rotulo,
       VC.mesesFabricacao,
       VC.entrada,
       VC.nfEntrada,
       VC.dataEntrada,
       VC.fabricacao,
       VC.vencimento
FROM T_RESULT AS R
       LEFT JOIN T_PRDVENDA AS V ON (R.prdno = V.prdno AND R.grade = V.grade)
       LEFT JOIN T_PRDCOMPRA AS C ON (R.prdno = C.prdno AND R.grade = C.grade)
       LEFT JOIN T_PRDVENCIMENTO AS VC ON (R.prdno = VC.prdno AND R.grade = VC.grade)
WHERE (:pesquisa = ''
  OR codigo LIKE @PESQUISA
  OR descricao LIKE @PESQUISA_LIKE
  OR R.grade LIKE @PESQUISA_LIKE
  OR fornStr LIKE @PESQUISA
  OR abrev LIKE @PESQUISA_LIKE
  OR tipo LIKE @PESQUISA
  OR cl LIKE @PESQUISA
  OR groupno LIKE @PESQUISA
  OR deptno LIKE @PESQUISA
  OR codBar LIKE @PESQUISA
  OR ncm LIKE @PESQUISA
  OR localizacao LIKE @PESQUISA
  OR rotulo LIKE @PESQUISA)
  AND (R.grade LIKE @GRADE OR @GRADE = '')

