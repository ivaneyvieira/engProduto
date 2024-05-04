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
SELECT P.no                                                                AS prdno,
       COALESCE(A.grade, L.grade, '')                                      AS grade,
       CAST(MID(COALESCE(A.localizacao, L.localizacao, ''), 1, 4) AS CHAR) AS loc
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdloc AS L
                 ON P.no = L.prdno
       LEFT JOIN sqldados.prdAdicional AS A
                 ON A.prdno = L.prdno
                   AND A.grade = L.grade
                   AND A.storeno = L.storeno
WHERE (L.storeno = 4)
GROUP BY prdno, grade;

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
       I.qtty,
       A.marcaRecebimento
FROM sqldados.iprd AS I
       INNER JOIN sqldados.inv AS N
                  USING (invno)
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
  AND (N.type = 0)
  AND (N.invno = :invno OR :invno = 0);

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
SELECT N.storeno                                    AS loja,
       N.login                                      AS login,
       DATE(N.date)                                 AS data,
       DATE(N.issue_date)                           AS emissao,
       N.invno                                      AS ni,
       CONCAT(N.nfname, '/', N.invse)               AS nfEntrada,
       C.no                                         AS custno,
       N.vendno                                     AS vendno,
       V.name                                       AS fornecedor,
       N.grossamt / 100                             AS valorNF,
       N.ordno                                      AS pedComp,
       N.carrno                                     AS transp,
       N.auxLong2                                   AS cte,
       N.packages                                   AS volume,
       N.weight                                     AS peso,
  /*Produto*/
       P.no                                         AS prdno,
       TRIM(P.no)                                   AS codigo,
       COALESCE(B.barcodeList, TRIM(P.barcode), '') AS barcodeStrList,
       TRIM(MID(P.name, 1, 37))                     AS descricao,
       N.grade                                      AS grade,
       L.loc                                        AS localizacao,
       ROUND(N.qtty / 1000)                         AS quant,
       ROUND(E.estoque)                             AS estoque,
       IFNULL(N.marcaRecebimento, 0)                AS marca
FROM T_NOTA AS N
       LEFT JOIN sqldados.vend AS V
                 ON V.no = N.vendno
       LEFT JOIN custp AS C
                 ON C.cpf_cgc = V.cgc
       INNER JOIN sqldados.prd AS P
                  ON P.no = N.prdno
       LEFT JOIN T_BARCODE AS B
                 ON B.prdno = N.prdno
                   AND B.grade = N.grade
       LEFT JOIN T_LOC AS L
                 ON L.prdno = N.prdno
                   AND L.grade = N.grade
       LEFT JOIN T_EST AS E
                 ON E.prdno = N.prdno
                   AND E.grade = N.grade
WHERE (P.dereg & POW(2, 6)) = 0
  AND ((P.bits & POW(2, 13)) = 0);

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
  /*Produto*/
       prdno,
       codigo,
       barcodeStrList,
       descricao,
       grade,
       localizacao,
       quant,
       estoque,
       marca,
       :marca AS marcaSelecionada
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
       volume = @PESQUISA_NUM)
  AND (marca = :marca OR :marca = 999)
  AND (localizacao IN (:localizacao) OR 'TODOS' IN (:localizacao))
