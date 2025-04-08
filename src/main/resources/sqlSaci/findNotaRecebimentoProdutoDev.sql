USE sqldados;

SET SQL_MODE = '';

DO @DT := 20240420;

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
  `storeno`          smallint NOT NULL DEFAULT '0',
  `notaDevolucao`    varchar(14),
  `emissaoDevolucao` date,
  `valorDevolucao`   decimal(23, 4),
  `obsDevolucao`     char(160),
  `cancelado`        int,
  `nfo`              varchar(16),
  `motivo`           int,
  PRIMARY KEY (storeno, nfo, motivo, notaDevolucao)
)
SELECT storeno                             AS storeno,
       CONCAT(nfno, '/', nfse)             AS notaDevolucao,
       CAST(issuedate AS date)             AS emissaoDevolucao,
       grossamt / 100                      AS valorDevolucao,
       print_remarks                       AS obsDevolucao,
       status = 1                          AS cancelado,
       IF(LOCATE(' NFO ', print_remarks) > 0,
          SUBSTRING_INDEX(
              SUBSTRING(print_remarks,
                        LOCATE(' NFO ', print_remarks) + 5,
                        100), ' ', 1), '') AS nfo,
       CASE
         WHEN print_remarks REGEXP 'AVARIA'            THEN 1
         WHEN print_remarks REGEXP 'FAL.{1,10}TRANSP'  THEN 2
         WHEN print_remarks REGEXP 'FAL.{1,10}FAB'     THEN 3
         WHEN print_remarks REGEXP 'VENCIM|VENCID'     THEN 4
         WHEN print_remarks REGEXP 'SEM.{1,10}IDENTIF' THEN 5
         WHEN print_remarks REGEXP 'DESAC.{1,10}PED'   THEN 6
         WHEN print_remarks REGEXP 'DEFEITO.{1,10}FAB' THEN 7
         WHEN print_remarks REGEXP 'GARANTIA'          THEN 8
                                                       ELSE 0
       END                                 AS motivo
FROM
  sqldados.nf
WHERE issuedate >= 20241001
  AND tipo = 2
  AND status != 1;


DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno AS prdno, A.grade AS grade, TRIM(MID(A.localizacao, 1, 4)) AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE (A.storeno = 4);

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno, prdno, grade)
)
SELECT I.invno,
       I.prdno,
       I.grade,
       N.storeno,
       L.sname                                                        AS lojaSigla,
      /* IFNULL(A.login, '')                                            AS login,*/
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
       I.baseIcms / 100                                               AS baseIcms,
       I.icms / 100                                                   AS valIcms,
       I.ipiAmt / 100                                                 AS valIPI,
       I.icmsAliq / 100                                               AS icms,
       I.ipi / 100                                                    AS ipi,
       IF(N.bits & POW(2, 10) = 0, 0, I.m6) / 100                     AS frete,
       I.l6 / 100                                                     AS outDesp,
       I.icmsSubst / 100                                              AS icmsSubst,
       A.tipoDevolucao,
       A.quantDevolucao,
       IA.volume                                                      AS volumeDevolucao,
       IA.peso                                                        AS pesoDevolucao,
       IA.carrno                                                      AS transpDevolucao,
       CA.name                                                        AS transportadoraDevolucao,
       IA.cet                                                         AS cteDevolucao,
       CAST(IF(IA.dataDevolucao = 0, NULL, IA.dataDevolucao) AS date) AS dataDevolucao,
       IA.situacaoDev                                                 AS situacaoDev,
       UA.login                                                       AS userDevolucao
FROM
  sqldados.iprdAdicionalDev         AS A
    LEFT JOIN sqldados.inv          AS N
              USING (invno)
    LEFT JOIN sqldados.iprd         AS I
              USING (invno, prdno, grade)
    LEFT JOIN sqldados.carr         AS C
              ON C.no = N.carrno
    LEFT JOIN sqldados.store        AS L
              ON L.no = N.storeno
    LEFT JOIN sqldados.invAdicional AS IA
              ON IA.invno = A.invno
                AND IA.tipoDevolucao = A.tipoDevolucao
    LEFT JOIN sqldados.carr         AS CA
              ON CA.no = IA.carrno
    LEFT JOIN sqldados.users        AS UA
              ON UA.no = IA.userno
WHERE (N.bits & POW(2, 4) = 0)
  AND (N.date >= @DT)
  AND (N.date >= 20240101)
  AND (N.storeno IN (1, 2, 3, 4, 5, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND ((N.account IN ('2.01.20', '2.01.21', '4.01.01.04.02', '4.01.01.06.04', '6.03.01.01.01', '6.03.01.01.02')) OR
       (N.account IN ('2.01.25')) OR ((N.type = 1)) OR
       ((N.cfo = 1949 AND N.remarks LIKE '%RECLASS%UNID%')))
  AND (A.tipoDevolucao > 0)
GROUP BY I.invno, I.prdno, I.grade;

DROP TEMPORARY TABLE IF EXISTS T_EST;
CREATE TEMPORARY TABLE T_EST
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM((qtty_atacado + qtty_varejo) / 1000) AS estoque
FROM
  sqldados.stk                                                           AS S
    INNER JOIN ( SELECT prdno, grade FROM T_NOTA GROUP BY prdno, grade ) AS N
               USING (prdno, grade)
WHERE (storeno = :loja OR :loja = 0)
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_VENCIMENTO;
CREATE TEMPORARY TABLE T_VENCIMENTO
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno                            AS storeno,
       prdno                              AS prdno,
       grade                              AS grade,
       MAX(dataVenda)                     AS dataVenda,
       MAX(vendas)                        AS vendas,
       MAX(IF(num = 1, quantidade, NULL)) AS qtty01,
       MAX(IF(num = 1, vencimento, NULL)) AS venc01,
       MAX(IF(num = 2, quantidade, NULL)) AS qtty02,
       MAX(IF(num = 2, vencimento, NULL)) AS venc02,
       MAX(IF(num = 3, quantidade, NULL)) AS qtty03,
       MAX(IF(num = 3, vencimento, NULL)) AS venc03,
       MAX(IF(num = 4, quantidade, NULL)) AS qtty04,
       MAX(IF(num = 4, vencimento, NULL)) AS venc04
FROM
  sqldados.qtd_vencimento
GROUP BY storeno, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT N.storeno                                                   AS loja,
       N.lojaSigla                                                 AS lojaSigla,
       N.login                                                     AS login,
       DATE(N.date)                                                AS data,
       DATE(N.issue_date)                                          AS emissao,
       N.invno                                                     AS ni,
       TRIM(LEADING '0' FROM TRIM(CONCAT(N.nfname, '/', N.invse))) AS nfEntrada,
       C.no                                                        AS custno,
       N.vendno                                                    AS vendno,
       V.name                                                      AS fornecedor,
       N.grossamt / 100                                            AS valorNF,
       N.ordno                                                     AS pedComp,
       N.carrno                                                    AS transp,
       N.transportadora,
       N.auxLong2                                                  AS cte,
       N.packages                                                  AS volume,
       N.weight                                                    AS peso,
  /*Produto*/
       P.no                                                        AS prdno,
       TRIM(P.no)                                                  AS codigo,
       IF(N.grade = '', CONCAT(IFNULL(B.barcodeList, ''), IF(IFNULL(B.barcodeList, '') = '', '', ','), TRIM(P.barcode)),
          COALESCE(B.barcodeList, TRIM(P.barcode), ''))            AS barcodeStrList,
       IF(N.grade = '', IFNULL(TRIM(P.barcode), ''),
          COALESCE(B.barcodeList, TRIM(P.barcode), ''))            AS barcodeStrListEntrada,
       TRIM(MID(P.name, 1, 37))                                    AS descricao,
       TRIM(MID(P.name, 37, 3))                                    AS un,
       N.grade                                                     AS grade,
       IFNULL(L.localizacao, '')                                   AS localizacao,
       IFNULL(LS.localizacao, '')                                  AS localizacaoSaci,
       P.mfno                                                      AS vendnoProduto,
       ROUND(N.qtty)                                               AS quant,
       ROUND(E.estoque)                                            AS estoque,
       IFNULL(N.marcaRecebimento, 0)                               AS marca,
       P.mfno_ref                                                  AS refFabrica,
       N.cfop                                                      AS cfop,
       N.cst                                                       AS cst,
       @VALID := IF((tipoGarantia = 3 AND garantia = 999) || (tipoGarantia = 2 AND garantia > 0), 'S',
                    'N')                                           AS validadeValida,
       IF(@VALID = 'S', garantia, NULL)                            AS validade,
       CASE tipoGarantia
         WHEN 0 THEN 'Dias'
         WHEN 1 THEN 'Semanas'
         WHEN 2 THEN 'Meses'
         WHEN 3 THEN 'Anos'
                ELSE ''
       END                                                         AS tipoValidade,
       garantia                                                    AS tempoValidade,
       vencimento                                                  AS vencimento,
       ER.no                                                       AS usernoRecebe,
       ER.login                                                    AS usuarioRecebe,
       observacaoNota                                              AS observacaoNota,
       tipoNota                                                    AS tipoNota,
       selecionado                                                 AS selecionado,
       VC.dataVenda                                                AS dataVenda,
       VC.vendas                                                   AS vendas,
       VC.qtty01                                                   AS qtty01,
       VC.venc01                                                   AS venc01,
       VC.qtty02                                                   AS qtty02,
       VC.venc02                                                   AS venc02,
       VC.qtty03                                                   AS qtty03,
       VC.venc03                                                   AS venc03,
       VC.qtty04                                                   AS qtty04,
       VC.venc04                                                   AS venc04,
       N.valorUnit,
       N.valorTotal,
       N.valorDesconto,
       N.baseIcms,
       N.valIcms,
       N.valIPI,
       N.icms,
       N.ipi,
       N.frete,
       N.outDesp,
       N.icmsSubst,
       IFNULL(tipoDevolucao, 0)                                    AS tipoDevolucao,
       IF(IFNULL(tipoDevolucao, 0) = 0, 0,
          IFNULL(quantDevolucao, 0))                               AS quantDevolucao,
       volumeDevolucao,
       pesoDevolucao,
       transpDevolucao,
       transportadoraDevolucao,
       cteDevolucao,
       dataDevolucao,
       IFNULL(situacaoDev, 0)                                      AS situacaoDev,
       userDevolucao
FROM
  T_NOTA                       AS N
    LEFT JOIN  T_VENCIMENTO    AS VC
               USING (storeno, prdno, grade)
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
    LEFT JOIN  T_LOC           AS L
               ON L.prdno = N.prdno AND L.grade = N.grade
    LEFT JOIN  sqldados.prdloc AS LS
               ON LS.prdno = N.prdno AND LS.grade = N.grade AND LS.storeno = 4
    LEFT JOIN  T_EST           AS E
               ON E.prdno = N.prdno AND E.grade = N.grade;

SELECT loja,
       lojaSigla,
       login,
       data,
       emissao,
       ni,
       nfEntrada,
       custno,
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
       prdno,
       codigo,
       barcodeStrList,
       barcodeStrListEntrada,
       descricao,
       grade,
       vendnoProduto,
       localizacao,
       localizacaoSaci,
       quant,
       estoque,
       marca,
       refFabrica,
       cfop,
       cst,
       un,
       validadeValida,
       validade,
       vencimento,
       tipoValidade,
       tempoValidade,
       observacaoNota,
       tipoNota,
       selecionado,
       dataVenda,
       vendas,
       qtty01,
       venc01,
       qtty02,
       venc02,
       qtty03,
       venc03,
       qtty04,
       venc04,
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
       tipoDevolucao,
       quantDevolucao,
       pesoDevolucao,
       volumeDevolucao,
       transpDevolucao,
       transportadoraDevolucao,
       cteDevolucao,
       dataDevolucao,
       CASE
         WHEN TRIM(IFNULL(N.notaDevolucao, '')) = '' THEN 0
         WHEN Q.situacaoDev = 0                      THEN 1
                                                     ELSE Q.situacaoDev
       END AS situacaoDev,
       userDevolucao,
       notaDevolucao,
       emissaoDevolucao,
       valorDevolucao,
       obsDevolucao
FROM
  T_QUERY           AS Q
    LEFT JOIN T_NFO AS N
              ON Q.nfEntrada = N.nfo
                AND Q.loja = N.storeno
                AND Q.tipoDevolucao = N.motivo
HAVING (@PESQUISA = '' OR ni = @PESQUISA_NUM OR nfEntrada LIKE @PESQUISA_LIKE OR custno = @PESQUISA_NUM OR
        vendno = @PESQUISA_NUM OR fornecedor LIKE @PESQUISA_LIKE OR pedComp = @PESQUISA_NUM OR transp = @PESQUISA_NUM OR
        cte = @PESQUISA_NUM OR volume = @PESQUISA_NUM OR tipoValidade LIKE @PESQUISA_LIKE)
   AND (situacaoDev = :situacaoDev)
