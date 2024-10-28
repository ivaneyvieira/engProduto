USE sqldados;

DROP TABLE IF EXISTS T_PRD_ST;
CREATE TEMPORARY TABLE IF NOT EXISTS T_PRD_ST
(
  PRIMARY KEY (prdno)
)
SELECT no AS prdno
FROM sqldados.prd
WHERE no = :prdno;

DROP TABLE IF EXISTS T_LOJAS;
CREATE TEMPORARY TABLE IF NOT EXISTS T_LOJAS
(
  PRIMARY KEY (storeno)
)
SELECT no AS storeno
FROM sqldados.store
WHERE (no IN (1, 2, 3, 4, 5, 8));

DROP TABLE IF EXISTS T_SPEDST;
CREATE TEMPORARY TABLE T_SPEDST
SELECT 165          AS aliqPis,
       760          AS aliqCofins,
       0            AS auxLong1,
       0            AS auxLong2,
       0            AS auxLong3,
       0            AS auxLong4,
       0            AS auxLong5,
       0            AS auxLong6,
       0            AS auxLong7,
       0            AS auxLong8,
       0            AS auxMoney1,
       0            AS auxMoney2,
       0            AS auxMoney3,
       0            AS auxMoney4,
       0            AS auxMoney5,
       0            AS auxMoney6,
       0            AS auxMoney7,
       0            AS auxMoney8,
       L.storeno,
       cstIpi,
       cstPis,
       cstCofins,
       0            AS natReceita,
       0            AS cstIpiIn,
       S.auxShort5  AS cstPisIn,
       S.auxShort6  AS cstCofinsIn,
       18           AS bits,
       0            AS padByte,
       0            AS auxShort1,
       0            AS auxShort2,
       0            AS auxShort3,
       0            AS auxShort4,
       0            AS auxShort5,
       0            AS auxShort6,
       0            AS auxShort7,
       0            AS auxShort8,
       S.prdno,
       codenq,
       A.form_label AS auxStr1,
       P.taxno      AS auxStr2,
       ''           AS auxStr3,
       ''           AS auxStr4
FROM sqldados.spedprd AS S
       INNER JOIN T_PRD_ST AS T
                  USING (prdno)
       INNER JOIN sqldados.prdalq AS A
                  ON A.prdno = S.prdno
       INNER JOIN sqldados.prd AS P
                  ON P.no = S.prdno,
     T_LOJAS AS L;

REPLACE INTO sqldados.spedprdst(aliqPis, aliqCofins, auxLong1, auxLong2, auxLong3, auxLong4, auxLong5, auxLong6,
                                auxLong7, auxLong8, auxMoney1, auxMoney2, auxMoney3, auxMoney4, auxMoney5,
                                auxMoney6, auxMoney7, auxMoney8, storeno, cstIpi, cstPis, cstCofins, natReceita,
                                cstIpiIn, cstPisIn, cstCofinsIn, bits, padByte, auxShort1, auxShort2, auxShort3,
                                auxShort4, auxShort5, auxShort6, auxShort7, auxShort8, prdno, codenq, auxStr1,
                                auxStr2, auxStr3, auxStr4)
SELECT aliqPis,
       aliqCofins,
       auxLong1,
       auxLong2,
       auxLong3,
       auxLong4,
       auxLong5,
       auxLong6,
       auxLong7,
       auxLong8,
       auxMoney1,
       auxMoney2,
       auxMoney3,
       auxMoney4,
       auxMoney5,
       auxMoney6,
       auxMoney7,
       auxMoney8,
       storeno,
       cstIpi,
       cstPis,
       cstCofins,
       natReceita,
       cstIpiIn,
       cstPisIn,
       cstCofinsIn,
       bits,
       padByte,
       auxShort1,
       auxShort2,
       auxShort3,
       auxShort4,
       auxShort5,
       auxShort6,
       auxShort7,
       auxShort8,
       prdno,
       codenq,
       auxStr1,
       IF(storeno = 8, CASE auxStr1
                         WHEN 'NORMAL..' THEN '01'
                         WHEN 'REDUZI88' THEN '21'
                         WHEN 'REDUZI56' THEN '21'
                         WHEN 'REDUZI20' THEN '21'
                         ELSE auxStr2
                       END, auxStr2) AS auxStr2,
       auxStr3,
       auxStr4
FROM T_SPEDST