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
FROM sqldados.prdbar
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno                                                    AS prdno,
       A.grade                                                    AS grade,
       GROUP_CONCAT(DISTINCT MID(A.localizacao, 1, 4) ORDER BY 1) AS localizacaoList
FROM sqldados.prdAdicional AS A
WHERE (A.storeno = 4)
  AND (A.prdno = :prdno OR :prdno = '')
  AND (A.grade = :grade OR :grade = '')
  AND ((MID(A.localizacao, 1, 4) IN (:local)) OR ('TODOS' IN (:local)) OR (A.localizacao = ''))
GROUP BY A.prdno, A.grade;

DROP TEMPORARY TABLE IF EXISTS T_NOTA_FILE;
CREATE TEMPORARY TABLE T_NOTA_FILE
(
  PRIMARY KEY (invno)
)
SELECT invno, COUNT(*) AS quant
FROM sqldados.invAdicionalArquivos
GROUP BY invno;

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno, prdno, grade)
)
SELECT I.invno,
       I.prdno,
       I.grade,
       N.storeno,
       IFNULL(A.login, '') AS login,
       N.date,
       N.issue_date,
       N.nfname,
       N.invse,
       N.vendno,
       N.grossamt,
       N.ordno,
       N.carrno,
       N.auxLong2,
       N.weight,
       N.packages,
       SUM(I.qtty)         AS qtty,
       A.marcaRecebimento,
       A.validade,
       A.vencimento,
       I.s26               AS usernoRecebe,
       N.remarks           AS observacaoNota,
       IFNULL(F.quant, 0)  AS quantFile,
       CASE
         WHEN N.account IN ('2.01.20', '2.01.21', '4.01.01.04.02', '6.03.01.01.01', '6.03.01.01.02') THEN 'Recebimento'
         WHEN N.account IN ('2.01.25') THEN 'Devolução'
         WHEN N.type = 1 THEN 'Transferência'
         WHEN N.cfo = 1949 AND N.remarks LIKE '%RECLASS%UNID%' THEN 'Reclassificação'
         ELSE ''
       END                 AS tipoNota,
       selecionado         AS selecionado
FROM sqldados.iprd AS I
       INNER JOIN sqldados.inv AS N
                  USING (invno)
       LEFT JOIN T_NOTA_FILE AS F
                 ON F.invno = I.invno
       LEFT JOIN sqldados.iprdAdicional AS A
                 ON A.invno = I.invno
                   AND A.prdno = I.prdno
                   AND A.grade = I.grade
WHERE (N.bits & POW(2, 4) = 0)
  AND (N.date >= @DT)
  AND (N.date >= :dataInicial OR :dataInicial = 0)
  AND (N.date <= :dataFinal OR :dataFinal = 0)
  AND (N.storeno IN (1, 2, 3, 4, 5, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND ((:tipoNota IN ('R', 'T') AND
        N.account IN ('2.01.20', '2.01.21', '4.01.01.04.02', '6.03.01.01.01', '6.03.01.01.02')) OR
       (:tipoNota IN ('D', 'T') AND N.account IN ('2.01.25')) OR
       (:tipoNota IN ('X', 'T') AND (N.type = 1)) OR
       (:tipoNota IN ('C', 'T') AND (N.cfo = 1949 AND N.remarks LIKE '%RECLASS%UNID%')))
  AND (N.invno = :invno OR :invno = 0)
GROUP BY I.invno, I.prdno, I.grade;

DROP TEMPORARY TABLE IF EXISTS T_EST;
CREATE TEMPORARY TABLE T_EST
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM((qtty_atacado + qtty_varejo) / 1000) AS estoque
FROM sqldados.stk AS S
       INNER JOIN (SELECT prdno, grade FROM T_NOTA GROUP BY prdno, grade) AS N
                  USING (prdno, grade)
WHERE (storeno = :loja OR :loja = 0)
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT N.storeno                                                   AS loja,
       N.login                                                     AS login,
       DATE(N.date)                                                AS data,
       DATE(N.issue_date)                                          AS emissao,
       N.invno                                                     AS ni,
       CONCAT(N.nfname, '/', N.invse)                              AS nfEntrada,
       C.no                                                        AS custno,
       N.vendno                                                    AS vendno,
       V.name                                                      AS fornecedor,
       N.grossamt / 100                                            AS valorNF,
       N.ordno                                                     AS pedComp,
       N.carrno                                                    AS transp,
       N.auxLong2                                                  AS cte,
       N.packages                                                  AS volume,
       N.weight                                                    AS peso,
       N.quantFile                                                 AS quantFile,
  /*Produto*/
       P.no                                                        AS prdno,
       TRIM(P.no)                                                  AS codigo,
       IF(N.grade = '',
          CONCAT(IFNULL(B.barcodeList, ''), IF(IFNULL(B.barcodeList, '') = '', '', ','), TRIM(P.barcode)),
          COALESCE(B.barcodeList, TRIM(P.barcode), ''))            AS barcodeStrList,
       TRIM(MID(P.name, 1, 37))                                    AS descricao,
       N.grade                                                     AS grade,
       IFNULL(L.localizacaoList, '')                               AS localizacao,
       P.mfno                                                      AS vendnoProduto,
       ROUND(N.qtty / 1000)                                        AS quant,
       ROUND(E.estoque)                                            AS estoque,
       IFNULL(N.marcaRecebimento, 0)                               AS marca,
       @VALID := IF((tipoGarantia = 3 AND garantia = 999) ||
                    (tipoGarantia = 2 AND garantia > 0), 'S', 'N') AS validadeValida,
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
       selecionado                                                 AS selecionado
FROM T_NOTA AS N
       LEFT JOIN sqldados.users AS ER
                 ON ER.no = N.usernoRecebe
       LEFT JOIN sqldados.vend AS V
                 ON V.no = N.vendno
       LEFT JOIN sqldados.custp AS C
                 ON C.cpf_cgc = V.cgc
       INNER JOIN sqldados.prd AS P
                  ON P.no = N.prdno
       LEFT JOIN T_BARCODE AS B
                 ON B.prdno = N.prdno
                   AND B.grade = N.grade
       INNER JOIN T_LOC AS L
                  ON L.prdno = N.prdno
                    AND L.grade = N.grade
       LEFT JOIN T_EST AS E
                 ON E.prdno = N.prdno
                   AND E.grade = N.grade
WHERE (P.dereg & POW(2, 6)) = 0
  AND ((P.bits & POW(2, 13)) = 0)
  AND (P.no = :prdno OR :prdno = '')
  AND (N.grade = :grade OR :grade = '');

SELECT loja,
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
       cte,
       volume,
       peso,
       usernoRecebe,
       usuarioRecebe,
       quantFile,
  /*Produto*/
       prdno,
       codigo,
       barcodeStrList,
       descricao,
       grade,
       vendnoProduto,
       localizacao,
       quant,
       estoque,
       marca,
       :marca AS marcaSelecionada,
       validadeValida,
       validade,
       vencimento,
       tipoValidade,
       tempoValidade,
       observacaoNota,
       tipoNota,
       selecionado
FROM T_QUERY
WHERE (@PESQUISA = '' OR
       ni = @PESQUISA_NUM OR
       nfEntrada LIKE @PESQUISA_LIKE OR
       custno = @PESQUISA_NUM OR
       vendno = @PESQUISA_NUM OR
       fornecedor LIKE @PESQUISA_LIKE OR
       pedComp = @PESQUISA_NUM OR
       transp = @PESQUISA_NUM OR
       cte = @PESQUISA_NUM OR
       volume = @PESQUISA_NUM OR
       tipoValidade LIKE @PESQUISA_LIKE)
  AND (marca = :marca OR :marca = 999)
  AND ((:anexo = 'S' AND quantFile > 0) OR (:anexo = 'N' AND quantFile = 0) OR (:anexo = 'T'))
