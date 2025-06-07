USE sqldados;

SET SQL_MODE = '';

DO @DT := 20100101;

DO @LJ := :loja;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP TEMPORARY TABLE IF EXISTS T_BARCODE;
CREATE TEMPORARY TABLE T_BARCODE
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, GROUP_CONCAT(DISTINCT TRIM(barcode)) AS barcodeList
FROM
  sqldados.prdbar
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_NFO;
CREATE TEMPORARY TABLE T_NFO
(
  storeno          smallint NOT NULL DEFAULT 0,
  pdvno            int      NOT NULL DEFAULT 0,
  xano             int      NOT NULL DEFAULT 0,
  nfno             int               DEFAULT 0 NOT NULL,
  nfse             char(2)           DEFAULT '' NOT NULL,
  notaDevolucao    varchar(14),
  emissaoDevolucao date,
  valorDevolucao   decimal(23, 4),
  obsDevolucao     char(160),
  niDev            int,
  PRIMARY KEY (storeno, pdvno, xano),
  INDEX (niDev)
)
SELECT storeno                 AS storeno,
       pdvno                   AS pdvno,
       xano                    AS xano,
       nfno                    AS nfno,
       nfse                    AS nfse,
       CONCAT(nfno, '/', nfse) AS notaDevolucao,
       CAST(issuedate AS date) AS emissaoDevolucao,
       grossamt / 100          AS valorDevolucao,
       print_remarks           AS obsDevolucao,
       remarks                 AS obsGarantia,
       COALESCE(
           IF(LOCATE(' PED ', CONCAT(print_remarks, ' ', remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(print_remarks, ' ', remarks, ' '),
                                        LOCATE(' PED ', CONCAT(print_remarks, ' ', remarks, ' ')) + 5,
                                        200),
                              ' ', 1), NULL),
           IF(LOCATE(' NID ', CONCAT(print_remarks, ' ', remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(print_remarks, ' ', remarks, ' '),
                                        LOCATE(' NID ', CONCAT(print_remarks, ' ', remarks, ' ')) + 5,
                                        200),
                              ' ', 1), NULL),
           IF(LOCATE(' NI DEV ', CONCAT(print_remarks, ' ', remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(print_remarks, ' ', remarks, ' '),
                                        LOCATE(' NI DEV ', CONCAT(print_remarks, ' ', remarks, ' ')) + 8,
                                        200),
                              ' ', 1), NULL)
       )                       AS niDev
FROM
  sqldados.nf
WHERE issuedate >= @DT
  AND tipo IN (2)
  AND status != 1
/*  AND (print_remarks LIKE '%NID%' OR remarks LIKE '%NID%' OR print_remarks LIKE '%NI DEV%' OR remarks LIKE '%NI DEV%')*/
HAVING niDev IS NOT NULL;

INSERT IGNORE INTO T_NFO
(storeno, pdvno, xano, nfno, nfse, notaDevolucao, emissaoDevolucao, valorDevolucao, obsDevolucao, obsGarantia, niDev)
SELECT storeno                 AS storeno,
       pdvno                   AS pdvno,
       xano                    AS xano,
       nfno                    AS nfno,
       nfse                    AS nfse,
       CONCAT(nfno, '/', nfse) AS notaDevolucao,
       CAST(issuedate AS date) AS emissaoDevolucao,
       grossamt / 100          AS valorDevolucao,
       print_remarks           AS obsDevolucao,
       remarks                 AS obsGarantia,
       COALESCE(
           IF(LOCATE(' PED ', CONCAT(print_remarks, ' ', remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(print_remarks, ' ', remarks, ' '),
                                        LOCATE(' PED ', CONCAT(print_remarks, ' ', remarks, ' ')) + 5,
                                        100),
                              ' ', 1), NULL),
           IF(LOCATE(' NID ', CONCAT(print_remarks, ' ', remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(print_remarks, ' ', remarks, ' '),
                                        LOCATE(' NID ', CONCAT(print_remarks, ' ', remarks, ' ')) + 5,
                                        100),
                              ' ', 1), NULL),
           IF(LOCATE(' NI DEV ', CONCAT(print_remarks, ' ', remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(print_remarks, ' ', remarks, ' '),
                                        LOCATE(' NI DEV ', CONCAT(print_remarks, ' ', remarks, ' ')) + 8,
                                        100),
                              ' ', 1), NULL)
       )                       AS niDev
FROM
  sqldados.nf
WHERE issuedate >= 20250401
  AND tipo IN (0)
  AND paymno IN (71)
  AND status != 1
/*  AND (print_remarks LIKE '%NID%' OR remarks LIKE '%NID%' OR print_remarks LIKE '%NI DEV%' OR remarks LIKE '%NI DEV%')*/
HAVING niDev IS NOT NULL;

DROP TEMPORARY TABLE IF EXISTS T_ARQCOLETA;
CREATE TEMPORARY TABLE T_ARQCOLETA
(
  PRIMARY KEY (tipoDevolucao, numero)
)
SELECT tipoDevolucao, numero, SUM(filename LIKE '%COLETA%') AS countColeta, COUNT(DISTINCT filename) AS countArq
FROM
  sqldados.invAdicionalDevArquivo
GROUP BY tipoDevolucao, numero;

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
SELECT A.invno,
       A.seq,
       A.prdno,
       A.grade,
       N.storeno,
       L.sname                                                        AS lojaSigla,
       N.date,
       N.issue_date,
       N.nfname,
       N.invse,
       N.vendno,
       N.grossamt,
       N.ordno,
       N.carrno,
       C.name                                                         AS transportadora,
       N.auxLong2,
       N.weight,
       N.packages,
       SUM(I.qtty / 1000)                                             AS qtty,
       I.cfop,
       I.cstIcms                                                      AS cst,
       I.s26                                                          AS usernoRecebe,
       N.remarks                                                      AS observacaoNota,
       CASE
         WHEN N.account IN ('2.01.20', '2.01.21', '4.01.01.04.02', '4.01.01.06.04', '6.03.01.01.01', '6.03.01.01.02')
                                                               THEN 'Recebimento'
         WHEN N.account IN ('2.01.25')                         THEN 'Devolução'
         WHEN N.type = 1                                       THEN 'Transferência'
         WHEN N.cfo = 1949 AND N.remarks LIKE '%RECLASS%UNID%' THEN 'Reclassificação'
                                                               ELSE ''
       END                                                            AS tipoNota,
       SUM((I.qtty / 1000) * (I.fob4 / 10000)) / SUM(I.qtty / 1000)   AS valorUnit,
       SUM((I.qtty / 1000) * (I.fob4 / 10000))                        AS valorTotal,
       I.discount / 100                                               AS valorDesconto,
       ((SUM((I.qtty / 1000) * (I.fob4 / 10000)) + (I.ipiAmt / 100)) /
        (I.baseIcms / 100) - 1.00) * 100                              AS valorMva,
       I.baseIcms / 100                                               AS baseIcms,
       I.icms / 100                                                   AS valIcms,
       I.ipiAmt / 100                                                 AS valIPI,
       I.icmsAliq / 100                                               AS icms,
       I.ipi / 100                                                    AS ipi,
       IF(N.bits & POW(2, 10) = 0, 0, I.m6) / 100                     AS frete,
       I.l6 / 100                                                     AS outDesp,
       I.icmsSubst / 100                                              AS icmsSubst,
       A.numero                                                       AS numeroDevolucao,
       A.tipoDevolucao                                                AS tipoDevolucao,
       A.quantDevolucao                                               AS quantDevolucao,
       IFNULL(IA.observacao, '')                                      AS observacaoDev,
       IA.volume                                                      AS volumeDevolucao,
       IA.peso                                                        AS pesoDevolucao,
       IA.carrno                                                      AS transpDevolucao,
       CA.name                                                        AS transportadoraDevolucao,
       IA.cet                                                         AS cteDevolucao,
       IA.observacaoAdicional                                         AS observacaoAdicional,
       CAST(IF(IA.dataDevolucao = 0, NULL, IA.dataDevolucao) AS date) AS dataDevolucao,
       IA.situacaoDev                                                 AS situacaoDev,
       UA.login                                                       AS userDevolucao,
       IFNULL(AC.countColeta, 0)                                      AS countColeta,
       IFNULL(AC.countArq, 0)                                         AS countArq,
       CAST(IF(IA.dataColeta = 0, NULL, IA.dataColeta) AS date)       AS dataColeta,
       SUM(I.baseIcmsSubst / 100) / SUM(I.qtty / 1000)                AS baseSTUnit,
       IFNULL(X.nfekey, '')                                           AS chaveUlt,
       I.c1                                                           AS chaveSefaz,
       IFNULL(S.ncm, '')                                              AS ncm
FROM
  sqldados.iprdAdicionalDev          AS A
    INNER JOIN sqldados.inv          AS N
               USING (invno)
    LEFT JOIN  sqldados.invnfe       AS X
               USING (invno)
    LEFT JOIN  sqldados.iprd         AS I
               USING (invno, prdno, grade)
    LEFT JOIN  sqldados.carr         AS C
               ON C.no = N.carrno
    LEFT JOIN  sqldados.store        AS L
               ON L.no = N.storeno
    LEFT JOIN  T_ARQCOLETA           AS AC
               USING (tipoDevolucao, numero)
    LEFT JOIN  sqldados.invAdicional AS IA
               USING (invno, tipoDevolucao, numero)
    LEFT JOIN  sqldados.carr         AS CA
               ON CA.no = IA.carrno
    LEFT JOIN  sqldados.users        AS UA
               ON UA.no = IA.userno
    LEFT JOIN  sqldados.spedprd      AS S
               ON I.prdno = S.prdno
WHERE (N.bits & POW(2, 4) = 0)
  AND (N.date >= @DT)
  AND (N.storeno IN (1, 2, 3, 4, 5, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (A.tipoDevolucao > 0)
  AND (IFNULL(IA.situacaoDev, 0) = :situacaoDev OR :situacaoDev = 999)
GROUP BY A.invno, A.prdno, A.grade, A.numero, A.tipoDevolucao;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT S.no AS storeno, prdno, grade
FROM
  T_NOTA,
  sqldados.store AS S
WHERE S.no IN (1, 2, 3, 4, 5, 8)
  AND (no = :loja OR :loja = 0)
GROUP BY S.no, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_EST;
CREATE TEMPORARY TABLE T_EST
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, ((qtty_atacado + qtty_varejo) / 1000) AS estoque
FROM
  sqldados.stk       AS S
    INNER JOIN T_PRD AS N
               USING (storeno, prdno, grade)
GROUP BY prdno, grade;


DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT N.storeno                                                      AS loja,
       N.lojaSigla                                                    AS lojaSigla,
       DATE(N.date)                                                   AS dataEntrada,
       DATE(N.issue_date)                                             AS emissao,
       N.invno                                                        AS ni,
       TRIM(LEADING '0' FROM TRIM(CONCAT(N.nfname, '/', N.invse)))    AS nfEntrada,
       C.no                                                           AS custno,
       N.vendno                                                       AS vendno,
       V.name                                                         AS fornecedor,
       N.grossamt / 100                                               AS valorNF,
       N.ordno                                                        AS pedComp,
       N.carrno                                                       AS transp,
       N.transportadora,
       N.auxLong2                                                     AS cte,
       N.packages                                                     AS volume,
       N.weight                                                       AS peso,
  /*Produto*/
       N.seq                                                          AS seq,
       P.no                                                           AS prdno,
       TRIM(P.no)                                                     AS codigo,
       IF(N.grade = '', CONCAT(IFNULL(B.barcodeList, ''), IF(IFNULL(B.barcodeList, '') = '', '', ','), TRIM(P.barcode)),
          COALESCE(B.barcodeList, TRIM(P.barcode), ''))               AS barcodeStrList,
       IF(N.grade = '', IFNULL(TRIM(P.barcode), ''),
          COALESCE(B.barcodeList, TRIM(P.barcode), ''))               AS barcodeStrListEntrada,
       TRIM(MID(P.name, 1, 37))                                       AS descricao,
       TRIM(MID(P.name, 37, 3))                                       AS un,
       N.grade                                                        AS grade,
       P.mfno                                                         AS vendnoProduto,
       ROUND(N.qtty)                                                  AS quant,
       ROUND(E.estoque)                                               AS estoque,
       P.mfno_ref                                                     AS refFabrica,
       N.cfop                                                         AS cfop,
       N.cst                                                          AS cst,
       @VALID := IF((tipoGarantia = 3 AND garantia = 999) OR (tipoGarantia = 2 AND garantia > 0), 'S',
                    'N')                                              AS validadeValida,
       IF(@VALID = 'S', garantia, NULL)                               AS validade,
       CASE tipoGarantia
         WHEN 0 THEN 'Dias'
         WHEN 1 THEN 'Semanas'
         WHEN 2 THEN 'Meses'
         WHEN 3 THEN 'Anos'
                ELSE ''
       END                                                            AS tipoValidade,
       garantia                                                       AS tempoValidade,
       ER.no                                                          AS usernoRecebe,
       ER.login                                                       AS usuarioRecebe,
       observacaoNota                                                 AS observacaoNota,
       tipoNota                                                       AS tipoNota,
       N.valorUnit,
       N.valorTotal,
       N.valorDesconto,
       N.valorMva,
       N.baseIcms,
       N.valIcms,
       N.valIPI,
       N.icms,
       N.ipi,
       N.frete,
       N.outDesp,
       N.icmsSubst,
       numeroDevolucao                                                AS numeroDevolucao,
       IFNULL(tipoDevolucao, 0)                                       AS tipoDevolucao,
       IF(IFNULL(tipoDevolucao, 0) = 0, 0, IFNULL(quantDevolucao, 0)) AS quantDevolucao,
       volumeDevolucao,
       pesoDevolucao,
       transpDevolucao,
       transportadoraDevolucao,
       cteDevolucao,
       observacaoAdicional,
       dataDevolucao,
       IFNULL(situacaoDev, 0)                                         AS situacaoDev,
       userDevolucao,
       observacaoDev,
       countColeta,
       countArq,
       dataColeta,
       IFNULL(R.form_label, '')                                       AS rotulo,
       baseSTUnit                                                     AS baseSTUnit,
       chaveUlt                                                       AS chaveUlt,
       chaveSefaz                                                     AS chaveSefaz,
       ncm                                                            AS ncm,
       P.weight                                                       AS pesoLiquido,
       P.weight_g                                                     AS pesoBruto
FROM
  T_NOTA                       AS N
    LEFT JOIN  sqldados.users  AS ER
               ON ER.no = N.usernoRecebe
    LEFT JOIN  sqldados.vend   AS V
               ON V.no = N.vendno
    LEFT JOIN  sqldados.custp  AS C
               ON C.cpf_cgc = V.cgc
    INNER JOIN sqldados.prd    AS P
               ON P.no = N.prdno
    LEFT JOIN  T_BARCODE       AS B
               ON B.prdno = N.prdno AND B.grade = N.grade
    LEFT JOIN  T_EST           AS E
               ON E.prdno = N.prdno AND E.grade = N.grade
    LEFT JOIN  sqldados.prdalq AS R
               ON R.prdno = N.prdno;

DROP TEMPORARY TABLE IF EXISTS T_DUP;
CREATE TEMPORARY TABLE T_DUP
(
  PRIMARY KEY (invno)
)
SELECT invno, docno, MIN(duedate) AS duedate, SUM(amtdue) AS amtdue
FROM
  sqldados.invxa
WHERE invno IN ( SELECT ni FROM T_QUERY )
GROUP BY invno;

DROP TEMPORARY TABLE IF EXISTS T_RESULT;
CREATE TEMPORARY TABLE T_RESULT
SELECT loja,
       lojaSigla,
       dataEntrada,
       emissao,
       ni,
       nfEntrada,
       Q.custno,
       vendno,
       fornecedor,
       valorNF,
       pedComp,
       transp,
       transportadora,
       cte,
       volume,
       peso,
       usernoRecebe,
       usuarioRecebe,
  /*Produto*/
       seq,
       prdno,
       codigo,
       barcodeStrList,
       barcodeStrListEntrada,
       descricao,
       grade,
       vendnoProduto,
       quant,
       estoque,
       refFabrica,
       cfop,
       cst,
       un,
       validadeValida,
       validade,
       tipoValidade,
       tempoValidade,
       observacaoNota,
       tipoNota,
       valorUnit,
       valorTotal,
       valorDesconto,
       baseIcms,
       valIcms,
       valIPI,
       icms,
       ipi,
       frete,
       outDesp,
       icmsSubst,
       numeroDevolucao,
       tipoDevolucao,
       quantDevolucao,
       pesoDevolucao,
       volumeDevolucao,
       transpDevolucao,
       transportadoraDevolucao,
       cteDevolucao,
       dataDevolucao,
       Q.situacaoDev,
       userDevolucao,
       N.notaDevolucao         AS notaDevolucao,
       N.emissaoDevolucao      AS emissaoDevolucao,
       N.valorDevolucao        AS valorDevolucao,
       N.obsDevolucao          AS obsDevolucao,
       N.storeno               AS storeno,
       N.pdvno                 AS pdvno,
       N.xano                  AS xano,
       observacaoDev,
       dataColeta,
       observacaoAdicional,
       countColeta,
       countArq,
       rotulo,
       baseSTUnit,
       chaveUlt,
       chaveSefaz,
       ncm,
       pesoLiquido,
       pesoBruto,
       D.docno                 AS duplicata,
       CAST(D.duedate AS date) AS dataVencimentoDup,
       D.amtdue / 100          AS valorVencimentoDup
FROM
  T_QUERY           AS Q
    LEFT JOIN T_NFO AS N
              ON (N.niDev = Q.numeroDevolucao)
    LEFT JOIN T_DUP AS D
              ON Q.ni = D.invno;

DROP TEMPORARY TABLE IF EXISTS T_RESULT2;
CREATE TEMPORARY TABLE T_RESULT2
(
  PRIMARY KEY (tipoDevolucao, numeroDevolucao)
)
SELECT tipoDevolucao,
       numeroDevolucao,
       MAX(userDevolucao)    AS userDevolucao,
       MAX(notaDevolucao)    AS notaDevolucao,
       MAX(emissaoDevolucao) AS emissaoDevolucao,
       MAX(valorDevolucao)   AS valorDevolucao,
       MAX(obsDevolucao)     AS obsDevolucao,
       MAX(situacaoDev)      AS situacaoDev
FROM
  T_RESULT
GROUP BY tipoDevolucao, numeroDevolucao;

UPDATE T_RESULT AS R1 INNER JOIN T_RESULT2 AS R2 USING (tipoDevolucao, numeroDevolucao)
SET R1.userDevolucao    = R2.userDevolucao,
    R1.notaDevolucao    = R2.notaDevolucao,
    R1.emissaoDevolucao = R2.emissaoDevolucao,
    R1.valorDevolucao   = R2.valorDevolucao,
    R1.obsDevolucao     = R2.obsDevolucao,
    R1.situacaoDev      = R2.situacaoDev
WHERE R1.tipoDevolucao = R2.tipoDevolucao
  AND R1.numeroDevolucao = R2.numeroDevolucao;

UPDATE sqldados.invAdicional AS I INNER JOIN T_RESULT AS R
  ON I.invno = R.ni
    AND I.tipoDevolucao = R.tipoDevolucao
    AND I.numero = R.numeroDevolucao
SET I.situacaoDev = R.situacaoDev
WHERE I.situacaoDev != R.situacaoDev;

SELECT *
FROM
  T_RESULT AS R
WHERE (situacaoDev = :situacaoDev OR :situacaoDev = 999)
