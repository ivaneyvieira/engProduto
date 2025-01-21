USE sqldados;

SET SESSION SQL_BIG_SELECTS = 1;
SET SESSION SQL_SAFE_UPDATES = 0;

DO @LOJA := :loja;
DO @PRDNO := LPAD(TRIM(:codigo), 16, ' ');
DO @GRADE := :grade;
DO @DATA_INICIAL := :dataInicial;

DO @DATA_INICIO_MES := DATE_FORMAT(@DATA_INICIAL, '%Y%m01') * 1;
DO @DIA_ANTES := SUBDATE(@DATA_INICIAL, INTERVAL 1 DAY) * 1;
DO @DIA_MES_ANTERIOR := LAST_DAY(SUBDATE(@DATA_INICIAL, INTERVAL 1 MONTH));
DO @MES_ANTEIROR := DATE_FORMAT(@DIA_MES_ANTERIOR, '%Y%m');

DROP TABLE IF EXISTS Lojas;
CREATE TEMPORARY TABLE Lojas
(
  PRIMARY KEY (storeno)
)
SELECT no AS storeno
FROM
  sqldados.store
WHERE no = @LOJA
   OR @LOJA = 0;

DROP TABLE IF EXISTS Saldo;
CREATE TEMPORARY TABLE Saldo
SELECT storeno AS loja, prdno, grade, date, 'SaldoAnterior' AS tipo, qtty / 1000 AS quant
FROM
  sqldados.stkchk
    INNER JOIN Lojas
               USING (storeno)
WHERE (prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '')
  AND ym = @MES_ANTEIROR;

/*
SELECT storeno                                 AS loja,
       prdno,
       grade,
       1                                       AS date,
       'SaldoAnterior'                         AS tipo,
       (qtty_atacado + stk.qtty_varejo) / 1000 AS quant
FROM sqldados.stk
       INNER JOIN Lojas
                  USING (storeno)
WHERE (prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '')*/

DROP TABLE IF EXISTS PreSaida;
CREATE TEMPORARY TABLE PreSaida
(
  cost BIGINT(15)
)
SELECT P.storeno,
       P.pdvno,
       P.xano,
       P.storeno                   AS loja,
       prdno,
       grade,
       date,
       CONCAT(P.nfno, '/', P.nfse) AS doc,
       (-(price) * 100 * qtty)     AS cost,
       'NF Saida'                  AS tipo,
       (-qtty * 1000 / 1000)       AS quant
FROM
  sqldados.xaprd           AS P
    INNER JOIN Lojas
               USING (storeno)
    INNER JOIN sqldados.nf AS N
               ON P.storeno = N.storeno AND P.pdvno = N.pdvno AND P.xano = N.xano
WHERE N.status <> 1
  AND date BETWEEN @DATA_INICIO_MES AND @DIA_ANTES
  AND N.cfo NOT IN (5922, 6922)
  AND (prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '');

DROP TABLE IF EXISTS TXA;
CREATE TEMPORARY TABLE TXA
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT P.storeno, P.pdvno, P.xano
FROM
  sqldados.xaprd           AS P
    INNER JOIN Lojas
               USING (storeno)
    INNER JOIN sqldados.nf AS N
               ON P.storeno = N.storeno AND P.pdvno = N.pdvno AND P.xano = N.xano
WHERE date BETWEEN @DATA_INICIO_MES AND @DIA_ANTES
  AND (prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '')
GROUP BY storeno, pdvno, xano;

DROP TABLE IF EXISTS NFSaida;
CREATE TEMPORARY TABLE NFSaida
SELECT loja, prdno, grade, date, 'NF Saida' AS tipo, SUM(quant) AS quant
FROM
  PreSaida
GROUP BY loja, prdno, grade, date;

DROP TABLE IF EXISTS NFCupom;
CREATE TEMPORARY TABLE NFCupom
SELECT X.storeno           AS loja,
       X.prdno,
       X.grade,
       X.date,
       'NF Cupom'          AS tipo,
       SUM(-X.qtty / 1000) AS quant,
       X.storeno,
       X.pdvno,
       X.xano
FROM
  xalog2                  AS X
    LEFT JOIN  TXA
               USING (storeno, pdvno, xano)
    INNER JOIN sqlpdv.pxa AS P
               USING (storeno, pdvno, xano)
    INNER JOIN Lojas      AS L
               ON L.storeno = X.storeno
WHERE icm_aliq & 4 = 0
  AND X.xatype <> 11
  AND X.qtty > 0
  AND X.date BETWEEN @DATA_INICIO_MES AND @DIA_ANTES
  AND P.nfse IN ('IF', '10')
  AND TXA.xano IS NULL
  AND (prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '')
GROUP BY X.storeno, prdno, grade, date;

DROP TABLE IF EXISTS Devolucao;
CREATE TEMPORARY TABLE Devolucao
SELECT X.storeno           AS loja,
       X.prdno,
       X.grade,
       X.date,
       'Devolucao'         AS tipo,
       SUM(-X.qtty / 1000) AS quant,
       X.storeno,
       X.pdvno,
       X.xano
FROM
  xalog2             AS X
    LEFT JOIN  TXA
               USING (storeno, pdvno, xano)
    INNER JOIN Lojas AS L
               ON L.storeno = X.storeno
WHERE icm_aliq & 4 = 0
  AND X.xatype = 11
  AND (X.doc LIKE 'DEVOL%' OR X.qtty > 0)
  AND TXA.xano IS NULL
  AND X.date BETWEEN @DATA_INICIO_MES AND @DIA_ANTES
  AND (prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '')
GROUP BY X.storeno, prdno, grade, date;

DROP TABLE IF EXISTS MovManual;
CREATE TEMPORARY TABLE MovManual
SELECT storeno AS loja, prdno, grade, date, 'Mov Manual' AS tipo, SUM(qtty / 1000) AS quant
FROM
  sqldados.stkmov
    INNER JOIN Lojas
               USING (storeno)
WHERE qtty <> 0
  AND MID(remarks, 36, 1) <> '1'
  AND date BETWEEN @DATA_INICIO_MES AND @DIA_ANTES
  AND (prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '')
GROUP BY loja, prdno, grade, date;

DROP TEMPORARY TABLE IF EXISTS NFFutura;
CREATE TEMPORARY TABLE NFFutura
(
  PRIMARY KEY (storeno, nfNfno, nfNfse)
)
SELECT storeno, nfno AS nfNfno, nfse AS nfNfse
FROM
  sqldados.nf
WHERE cfo IN (5922, 6922)
GROUP BY storeno, nfNfno, nfNfse;

DROP TABLE IF EXISTS NFEntrada;
CREATE TEMPORARY TABLE NFEntrada
SELECT I.storeno AS loja, P.prdno, P.grade, I.date AS date, 'NF Entrada' AS tipo, SUM(qtty / 1000) AS quant
FROM
  sqldados.inv               AS I
    LEFT JOIN  NFFutura      AS F
               USING (storeno, nfNfno, nfNfse)
    INNER JOIN Lojas
               USING (storeno)
    INNER JOIN sqldados.iprd AS P
               ON I.invno = P.invno
WHERE I.bits & POW(2, 4) = 0
  AND I.auxShort13 & POW(2, 15) = 0
  AND I.date BETWEEN @DATA_INICIO_MES AND @DIA_ANTES
  AND (P.prdno = @PRDNO OR @PRDNO = '')
  AND (grade = @GRADE OR @GRADE = '')
  AND F.storeno IS NULL
GROUP BY loja, P.prdno, P.grade, I.date;

DROP TABLE IF EXISTS Kardec;
CREATE TEMPORARY TABLE Kardec
SELECT loja AS storeno, prdno, grade, date, tipo, quant
FROM
  Saldo
UNION ALL
SELECT loja AS storeno, prdno, grade, date, tipo, quant
FROM
  NFCupom
UNION ALL
SELECT loja AS storeno, prdno, grade, date, tipo, quant
FROM
  Devolucao
UNION ALL
SELECT loja AS storeno, prdno, grade, date, tipo, quant
FROM
  NFSaida
UNION ALL
SELECT loja AS storeno, prdno, grade, date, tipo, quant
FROM
  MovManual
UNION ALL
SELECT loja AS storeno, prdno, grade, date, tipo, quant
FROM
  NFEntrada;

SELECT storeno AS storeno, prdno AS prdno, grade AS grade, CAST(@DIA_ANTES AS DATE) AS date, ROUND(SUM(quant)) AS quant
FROM
  Kardec
GROUP BY storeno, prdno, grade