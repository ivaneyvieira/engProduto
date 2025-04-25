USE sqldados;

SET SQL_MODE = '';

DO @DT := 20150101;

DO @PESQUISA_NUM := :pesquisa * 1;
DO @PESQUISA_STR := TRIM(:pesquisa);
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA_STR, '%');

DROP TEMPORARY TABLE IF EXISTS T_NFO;
CREATE TEMPORARY TABLE T_NFO
(
  storeno          smallint,
  notaDevolucao    varchar(14),
  emissaoDevolucao date,
  valorDevolucao   decimal(23, 4),
  pedGarantia      int,
  PRIMARY KEY (storeno, notaDevolucao),
  INDEX (pedGarantia)
)
SELECT storeno                                      AS storeno,
       CONCAT(nfno, '/', nfse)                      AS notaDevolucao,
       CAST(issuedate AS date)                      AS emissaoDevolucao,
       grossamt / 100                               AS valorDevolucao,
       @PEG1 := IF(LOCATE(' PEG ', CONCAT(remarks, ' ')) > 0,
                   SUBSTRING_INDEX(SUBSTRING(CONCAT(remarks, ' '),
                                             LOCATE(' PEG ', CONCAT(remarks, ' ')) + 5, 100),
                                   ' ', 1), '') * 1 AS pedGarantia1,
       @PEG2 := IF(LOCATE(' PED ', CONCAT(remarks, ' ')) > 0,
                   SUBSTRING_INDEX(SUBSTRING(CONCAT(remarks, ' '),
                                             LOCATE(' PED ', CONCAT(remarks, ' ')) + 5, 100),
                                   ' ', 1), '') * 1 AS pedGarantia2,
       IF(remarks LIKE '%GARANTIA%',
          CASE
            WHEN @PEG1 != 0 THEN @PEG1
            WHEN @PEG2 != 0 THEN @PEG2
                            ELSE 0
          END
         , '0') * 1                                 AS pedGarantia
FROM
  sqldados.nf
WHERE issuedate >= @DT
  AND tipo = 2
  AND status != 1
HAVING pedGarantia != 0;


DROP TEMPORARY TABLE IF EXISTS T_GARANTIA;
CREATE TEMPORARY TABLE T_GARANTIA
SELECT *
FROM
  sqldados.produtoEstoqueGarantia
WHERE (numero = :numero OR :numero = 0)
  AND (numloja = :numLoja OR :numLoja = 0)
  AND (data >= :dataInicial OR :dataInicial = 0)
  AND (data <= :dataFinal OR :dataFinal = 0);

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade
FROM
  T_GARANTIA
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ESTOQUE_LOJA;
CREATE TEMPORARY TABLE T_ESTOQUE_LOJA
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno,
       grade,
       SUM(IF(storeno = :numLoja, qtty_atacado + qtty_varejo, 0) / 1000) AS estoqueLoja,
       SUM((qtty_atacado + qtty_varejo) / 1000)                          AS estoqueLojas
FROM
  sqldados.stk
    INNER JOIN T_PRD
               USING (prdno, grade)
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ULTIMO_INVNO;
CREATE TEMPORARY TABLE T_ULTIMO_INVNO
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, MAX(invno) AS invno
FROM
  sqldados.iprd             AS I
    INNER JOIN sqldados.inv AS N
               USING (invno)
    INNER JOIN T_PRD        AS T
               USING (prdno, grade)
WHERE N.type = 0
  AND N.bits & POW(2, 4) = 0
  AND I.cfop NOT IN (1910, 2910, 1916, 2916)
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_ULTIMO_RECEB;
CREATE TEMPORARY TABLE T_ULTIMO_RECEB
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno                          AS prdno,
       grade                          AS grade,
       N.storeno                      AS lojaReceb,
       N.invno                        AS niReceb,
       CONCAT(N.nfname, '/', N.invse) AS nfoReceb,
       CAST(N.date AS date)           AS entradaReceb,
       vendno                         AS forReceb,
       V.name                         AS nforReceb,
       V.no IN (8907)                 AS temLote,
       I.cfop                         AS cfopReceb,
       MAX(IA.numero)                 AS numeroDevolucao,
       I.fob4 / 10000                 AS valorUnitario
FROM
  sqldados.inv                       AS N
    LEFT JOIN  sqldados.vend         AS V
               ON N.vendno = V.no
    INNER JOIN T_ULTIMO_INVNO        AS U
               USING (invno)
    INNER JOIN sqldados.iprd         AS I
               USING (invno, prdno, grade)
    LEFT JOIN  sqldados.invAdicional AS IA
               ON IA.invno = U.invno
                 AND IA.tipoDevolucao = 8
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_LOC_APP;
CREATE TEMPORARY TABLE T_LOC_APP
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT P.storeno,
       P.prdno,
       P.grade,
       P.localizacao AS locApp
FROM
  sqldados.prdAdicional   AS P
    INNER JOIN T_GARANTIA AS A
               ON P.storeno = A.numloja
                 AND P.prdno = A.prdno
                 AND P.grade = A.grade
GROUP BY P.storeno, P.prdno, P.grade;

DROP TEMPORARY TABLE IF EXISTS T_BARCODE;
CREATE TEMPORARY TABLE T_BARCODE
(
  PRIMARY KEY (prdno, grade)
)
SELECT P.no                                                           AS prdno,
       IFNULL(B.grade, '')                                            AS grade,
       MAX(TRIM(IF(B.grade IS NULL,
                   IFNULL(IF(LENGTH(TRIM(P.barcode)) = 13,
                             P.barcode, NULL), P2.gtin), B.barcode))) AS codbar
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prd2   AS P2
              ON P.no = P2.prdno
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno AND B.grade != '' AND LENGTH(TRIM(B.barcode)) = 13
WHERE P.no IN ( SELECT DISTINCT prdno FROM T_GARANTIA )
GROUP BY P.no, B.grade
HAVING codbar != '';

SELECT numero,
       numloja,
       S.sname                       AS lojaSigla,
       data,
       hora,
       usuario,
       A.prdno,
       TRIM(MID(P.name, 1, 37))      AS descricao,
       A.grade,
       L.locApp,
       B.codbar                      AS barcode,
       TRIM(P.mfno_ref)              AS ref,
       ROUND(EL.estoqueLoja)         AS estoqueLoja,
       estoqueReal                   AS estoqueDev,
       ROUND(EL.estoqueLojas)        AS estoqueLojas,
       O.observacao                  AS observacao,
       UR.lojaReceb                  AS lojaReceb,
       UR.niReceb                    AS niReceb,
       UR.nfoReceb                   AS nfoReceb,
       UR.entradaReceb               AS entradaReceb,
       UR.forReceb                   AS forReceb,
       UR.nforReceb                  AS nforReceb,
       A.loteDev                     AS loteDev,
       UR.temLote                    AS temLote,
       UR.cfopReceb                  AS cfopReceb,
       IFNULL(UR.numeroDevolucao, 0) AS numeroDevolucao,
       UR.valorUnitario              AS valorUnitario,
       N.notaDevolucao               AS nfdGarantia,
       N.emissaoDevolucao            AS dataNfdGarantia,
       N.notaDevolucao IS NULL       AS pendente
FROM
  T_GARANTIA                                     AS A
    LEFT JOIN T_ESTOQUE_LOJA                     AS EL
              USING (prdno, grade)
    LEFT JOIN T_ULTIMO_RECEB                     AS UR
              USING (prdno, grade)
    LEFT JOIN T_BARCODE                          AS B
              ON B.prdno = A.prdno
                AND B.grade = A.grade
    LEFT JOIN sqldados.produtoObservacaoGarantia AS O
              USING (numero, numloja)
    LEFT JOIN T_NFO                              AS N
              ON N.storeno = A.numloja
                AND N.pedGarantia = A.numero
    LEFT JOIN sqldados.store                     AS S
              ON S.no = A.numloja
    LEFT JOIN sqldados.prd                       AS P
              ON P.no = A.prdno
    LEFT JOIN T_LOC_APP                          AS L
              ON L.storeno = A.numloja
                AND L.prdno = A.prdno
                AND L.grade = A.grade
WHERE (
  (N.notaDevolucao IS NOT NULL AND :devolvido = 'F') OR
  (N.notaDevolucao IS NULL AND :devolvido = 'P') OR
  (:devolvido = 'T')
  )
  AND (
  @PESQUISA_STR = '' OR
  numero = @PESQUISA_NUM OR
  S.sname LIKE @PESQUISA_LIKE OR
  UR.forReceb = @PESQUISA_NUM OR
  UR.nforReceb LIKE @PESQUISA_LIKE
  )
