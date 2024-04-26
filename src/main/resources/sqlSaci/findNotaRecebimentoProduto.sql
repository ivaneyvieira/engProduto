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

DROP TEMPORARY TABLE IF EXISTS T_EST;
CREATE TEMPORARY TABLE T_EST
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM((qtty_atacado + qtty_varejo) / 1000) AS estoque
FROM sqldados.stk
WHERE storeno IN (1, 2, 3, 4, 5, 8)
  AND (storeno = :loja OR :loja = 0)
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT N.storeno                      AS loja,
       DATE(N.issue_date)             AS data,
       N.invno                        AS ni,
       CONCAT(N.nfname, '/', N.invse) AS nfEntrada,
       C.no                           AS custno,
       N.vendno                       AS vendno,
       V.name                         AS fornecedor,
       N.grossamt / 100               AS valorNF,
       N.ordno                        AS pedComp,
       N.carrno                       AS transp,
       ''                             AS cte,
       0                              AS volume,
       0                              AS peso,
  /*Produto*/
       P.no                           AS prdno,
       TRIM(P.no)                     AS codigo,
       B.barcodeList                  AS barcodeStrList,
       TRIM(MID(P.name, 1, 37))       AS descricao,
       I.grade                        AS grade,
       L.loc                          AS localizacao,
       ROUND(I.qtty / 1000)           AS quant,
       ROUND(E.estoque)               AS estoque
FROM sqldados.inv AS N
       LEFT JOIN sqldados.vend AS V
                 ON V.no = N.vendno
       LEFT JOIN custp AS C
                 ON C.cpf_cgc = V.cgc
       INNER JOIN sqldados.iprd AS I
                  ON N.invno = I.invno
       INNER JOIN sqldados.prd AS P
                  ON P.no = I.prdno
       LEFT JOIN T_BARCODE AS B
                 ON B.prdno = I.prdno
                   AND B.grade = I.grade
       LEFT JOIN T_LOC AS L
                 ON L.prdno = I.prdno
                   AND L.grade = I.grade
       INNER JOIN T_EST AS E
                  ON E.prdno = I.prdno
                    AND E.grade = I.grade
WHERE N.bits & POW(2, 4) = 0
  AND N.issue_date >= @DT
  AND N.storeno IN (1, 2, 3, 4, 5, 8)
  AND (N.storeno = :loja OR :loja = 0)
  AND N.type = 0;

SELECT loja,
       data,
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
       estoque
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