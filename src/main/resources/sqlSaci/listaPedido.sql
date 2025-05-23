USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DO @TIPO := :tipo;

DO @DATA := SUBDATE(CURDATE(), 90) * 1;

DROP TEMPORARY TABLE IF EXISTS T_TIPO;
CREATE TEMPORARY TABLE T_TIPO
(
  PRIMARY KEY (storeno, ordno)
)
SELECT DISTINCT storeno, ordno
FROM
  sqldados.eoprdf
WHERE (((@TIPO = 'R') AND (eoprdf.bits & POW(2, 1))) OR ((@TIPO = 'E') AND (NOT eoprdf.bits & POW(2, 1))))
  AND (storeno IN (2, 3, 4, 5, 8))
  AND (date >= @DATA);

DROP TEMPORARY TABLE IF EXISTS T2;
CREATE TEMPORARY TABLE T2
(
  PRIMARY KEY (storeno, ordno)
)
SELECT pxa.storeno,
       pxa.pdvno,
       pxa.eordno                                       AS ordno,
       pxa.time,
       pxanf.fre_amt,
       MAX(IF(pxa.cfo IN (5922, 6922), pxa.date, NULL)) AS data_venda,
       MAX(IF(pxa.cfo IN (5922, 6922), pxa.nfno, NULL)) AS nfno_venda,
       MAX(IF(pxa.cfo IN (5922, 6922), pxa.nfse, NULL)) AS nfse_venda,
       MAX(IF(pxa.cfo IN (5922, 6922), pxa.amt, NULL))  AS valor_venda,
       MAX(IF(pxa.cfo IN (5117, 6117), pxa.date, NULL)) AS data_entrega,
       MAX(IF(pxa.cfo IN (5117, 6117), pxa.nfno, NULL)) AS nfno_entrega,
       MAX(IF(pxa.cfo IN (5117, 6117), pxa.nfse, NULL)) AS nfse_entrega,
       MAX(IF(pxa.cfo IN (5117, 6117), pxa.amt, NULL))  AS valor_entrega
FROM
  sqlpdv.pxa
    INNER JOIN T_TIPO AS T
               ON T.storeno = pxa.storeno AND pxa.eordno = T.ordno
    LEFT JOIN  sqlpdv.pxanf
               ON (pxa.xano = pxanf.xano AND pxa.storeno = pxanf.storeno AND pxa.pdvno = pxanf.pdvno)
WHERE (pxa.storeno IN (2, 3, 4, 5, 8))
  AND (pxa.storeno = :storeno OR :storeno = 0)
  AND pxa.cfo IN (5922, 6922, 5117, 6117)
  AND pxa.date >= @DATA
GROUP BY pxa.storeno, pxa.eordno;

DROP TEMPORARY TABLE IF EXISTS T2_ECOMERCE;
CREATE TEMPORARY TABLE T2_ECOMERCE
(
  PRIMARY KEY (storeno, ordno)
)
SELECT E.storeno,
       E.pdvno,
       E.ordno                          AS ordno,
       E.l4                             AS time,
       E.other                          AS fre_amt,
       IFNULL(P.amt, E.amount)          AS data_venda,
       CAST(IFNULL(P.nfno, '') AS CHAR) AS nfno_venda,
       IFNULL(P.nfse, '')               AS nfse_venda,
       IFNULL(P.amt, E.amount)          AS valor_venda,
       IFNULL(P.amt, E.amount)          AS data_entrega,
       CAST(IFNULL(P.nfno, '') AS CHAR) AS nfno_entrega,
       IFNULL(P.nfse, '')               AS nfse_entrega,
       IFNULL(P.amt, E.amount)          AS valor_entrega
FROM
  sqldados.eord          AS E
    LEFT JOIN sqlpdv.pxa AS P
              ON P.storeno = E.storeno AND E.ordno = P.eordno AND P.nfno != ''
WHERE (E.storeno IN (4))
  AND E.status NOT IN (3, 5)
  AND (E.storeno = :storeno OR :storeno = 0)
  AND (E.empno = 440)
  AND P.date >= @DATA
GROUP BY E.storeno, E.ordno;

DROP TEMPORARY TABLE IF EXISTS T_CARGA;
CREATE TEMPORARY TABLE T_CARGA
(
  PRIMARY KEY (storeno, pdvno, xano),
  INDEX (storeno, ordno)
)
SELECT P.storeno,
       P.storenoStk,
       P.pdvno,
       P.xano,
       N.auxLong1       AS ordno,
       T2.time          AS time,
       T2.fre_amt       AS fre_amt,
       T2.data_venda    AS data_venda,
       T2.nfno_venda    AS nfno_venda,
       T2.nfse_venda    AS nfse_venda,
       T2.valor_venda   AS valor_venda,
       T2.data_entrega  AS data_entrega,
       T2.nfno_entrega  AS nfno_entrega,
       T2.nfse_entrega  AS nfse_entrega,
       T2.valor_entrega AS valor_entrega,
       N.c2             AS dados
FROM
  sqldados.nfrprd           AS P
    INNER JOIN sqldados.nfr AS N
               USING (storeno, pdvno, xano)
    INNER JOIN T2
               ON T2.storeno = N.storeno AND T2.ordno = N.auxLong1
WHERE (P.storeno = :storeno OR :storeno = 0)
  AND P.storeno != P.storenoStk
  AND N.date > 20240401
  AND P.optionEntrega % 10 = 4
  AND N.nfse != 3
GROUP BY storeno, pdvno, xano;

DROP TEMPORARY TABLE IF EXISTS VENDA_CARGA;
CREATE TEMPORARY TABLE VENDA_CARGA
SELECT EO.storeno                                                                                      AS loja,
       storenoStk                                                                                      AS storenoStk,
       S.name                                                                                          AS nomeLoja,
       S.sname                                                                                         AS siglaLoja,
       EO.ordno                                                                                        AS pedido,
       MID(T2.dados, 1, 1)                                                                             AS marca,
       MID(T2.dados, 3, 1)                                                                             AS separado,
       MID(T2.dados, 4, 1)                                                                             AS zonaCarga,
       MID(T2.dados, 5, 8) * 1                                                                         AS entrega,
       CAST(P.date AS DATE)                                                                            AS data,
       CAST(IF(EO.dataEntrega = 0, NULL, EO.dataEntrega) AS DATE)                                      AS dataEntrega,
       EO.pdvno                                                                                        AS pdvno,
       SEC_TO_TIME(P.time)                                                                             AS hora,
       T2.pdvno                                                                                        AS pdvnoVenda,
       IFNULL(CAST(T2.nfno_venda AS CHAR), '')                                                         AS nfnoFat,
       IFNULL(T2.nfse_venda, '')                                                                       AS nfseFat,
       IF(T2.data_venda = 0, NULL, CAST(T2.data_venda AS DATE))                                        AS dataFat,
       SEC_TO_TIME(nff2.auxLong4)                                                                      AS horaFat,
       (valor_venda / 100)                                                                             AS valorFat,
       IFNULL(CAST(T2.nfno_entrega AS CHAR), '')                                                       AS nfnoEnt,
       IFNULL(T2.nfse_entrega, '')                                                                     AS nfseEnt,
       IF(T2.data_entrega = 0, NULL, CAST(T2.data_entrega AS DATE))                                    AS dataEnt,
       SEC_TO_TIME(nfe2.auxLong4)                                                                      AS horaEnt,
       (valor_entrega / 100)                                                                           AS valorEnt,
       IFNULL(E.no, 0)                                                                                 AS vendno,
       CAST(CONCAT(E.no, '-', E.name) AS CHAR)                                                         AS vendedor,
       IFNULL(C.no, 0)                                                                                 AS custno,
       CAST(CONCAT(LPAD(C.no * 1, 6, '0'), '-', C.name) AS CHAR)                                       AS cliente,
       CAST(CONCAT('(', IFNULL(CA.ddd, LEFT(C.ddd, 3)), ')', IFNULL(CA.tel, LEFT(C.tel, 10))) AS CHAR) AS foneCliente,
       CAST(RPAD(IFNULL(CA.city, C.city1), 20, ' ') AS CHAR)                                           AS cidade,
       CAST(IFNULL(CA.state, C.state1) AS CHAR)                                                        AS estado,
       C.add1                                                                                          AS endereco,
       C.nei1                                                                                          AS bairro,
       CAST(RPAD(IFNULL(CA.addr, C.add1), 60, ' ') AS CHAR)                                            AS enderecoEntrega,
       RPAD(IFNULL(CA.nei, C.nei1), 25, ' ')                                                           AS bairroEntrega,
       IFNULL(T2.fre_amt, 0) / 100                                                                     AS frete,
       EO.amount / 100                                                                                 AS valor,
       @TIPO                                                                                           AS status,
       IFNULL(A.name, '')                                                                              AS area,
       IFNULL(R.name, '')                                                                              AS rota,
       IF(LEFT(OBS.remarks__480, 2) = 'EF ', LEFT(OBS.remarks__480, 11), ' ')                          AS obs,
       A.no                                                                                            AS codArea,
       EO.userno                                                                                       AS userno,
       IFNULL(U.name, '')                                                                              AS username,
       CAST(IF(EO.l9 = 0, NULL, EO.l9) AS DATE)                                                        AS dataPrint,
       SEC_TO_TIME(IF(EO.l8 = 0, NULL, EO.l8))                                                         AS horaPrint,
       EO.l5                                                                                           AS userPrint,
       RPAD(IFNULL(MID(O.remarks__480, 1, 80), ' '), 80, ' ')                                          AS obs1,
       RPAD(IFNULL(MID(O.remarks__480, 81, 80), ' '), 80, ' ')                                         AS obs2,
       RPAD(IFNULL(MID(O.remarks__480, 161, 80), ' '), 80, ' ')                                        AS obs3,
       RPAD(IFNULL(MID(O.remarks__480, 241, 80), ' '), 80, ' ')                                        AS obs4,
       RPAD(IFNULL(MID(O.remarks__480, 321, 80), ' '), 80, ' ')                                        AS obs5,
       RPAD(IFNULL(MID(O.remarks__480, 401, 80), ' '), 80, ' ')                                        AS obs6,
       RPAD(IFNULL(MID(O.remarks__480, 481, 80), ' '), 80, ' ')                                        AS obs7,
       @TIPO                                                                                           AS tipo,
       paym.name                                                                                       AS metodo,
       nfe.remarks                                                                                     AS obsNota
FROM
  T_CARGA                     AS T2
    LEFT JOIN sqldados.eord   AS EO
              ON (T2.storeno = EO.storeno AND T2.ordno = EO.ordno)
    LEFT JOIN sqldados.store  AS S
              ON S.no = EO.storeno
    LEFT JOIN sqldados.eordrk AS O
              ON (O.storeno = EO.storeno AND O.ordno = EO.ordno)
    LEFT JOIN sqldados.ctadd  AS CA
              ON (EO.custno = CA.custno AND CA.seqno = EO.custno_addno)
    LEFT JOIN sqldados.users  AS U
              ON U.no = EO.userno
    LEFT JOIN sqldados.custp  AS C
              ON (C.no = EO.custno)
    LEFT JOIN sqldados.emp    AS E
              ON (E.no = EO.empno)
    LEFT JOIN sqldados.paym
              ON (paym.no = EO.paymno)
    LEFT JOIN sqlpdv.pxa      AS P
              ON (EO.storeno = P.storeno AND EO.ordno = P.eordno AND EO.nfno_futura = P.nfno AND
                  EO.nfse_futura = P.nfse)
    LEFT JOIN sqldados.ctadd  AS AD
              ON (C.no = AD.custno AND AD.seqno = EO.custno_addno)
    LEFT JOIN sqldados.route  AS R
              ON (AD.routeno = R.no)
    LEFT JOIN sqldados.area   AS A
              ON (A.no = R.areano)
    LEFT JOIN sqldados.nf     AS nff
              ON (T2.nfno_venda = nff.nfno AND T2.nfse_venda = nff.nfse AND T2.storeno = nff.storeno)
    LEFT JOIN sqldados.nf2    AS nff2
              ON nff.storeno = nff2.storeno AND nff.pdvno = nff2.pdvno AND nff.xano = nff2.xano
    LEFT JOIN sqldados.nf     AS nfe
              ON (T2.nfno_entrega = nfe.nfno AND T2.nfse_entrega = nfe.nfse AND T2.storeno = nfe.storeno)
    LEFT JOIN sqldados.nf2    AS nfe2
              ON nfe.storeno = nfe2.storeno AND nfe.pdvno = nfe2.pdvno AND nfe.xano = nfe2.xano
    LEFT JOIN sqldados.eordrk AS OBS
              ON (OBS.storeno = EO.storeno AND OBS.ordno = EO.ordno)
WHERE (EO.date >= @DATA)
  AND (nff.status <> 1 OR nff.status IS NULL)
  AND (P.date >= :dataInicial OR :dataInicial = 0)
  AND (P.date <= :dataFinal OR :dataFinal = 0)
GROUP BY T2.storeno, T2.ordno;

DROP TEMPORARY TABLE IF EXISTS VENDA_NORMAL;
CREATE TEMPORARY TABLE VENDA_NORMAL
SELECT EO.storeno                                                                                      AS loja,
       EO.storeno                                                                                      AS storenoStk,
       S.name                                                                                          AS nomeLoja,
       S.sname                                                                                         AS siglaLoja,
       EO.ordno                                                                                        AS pedido,
       MID(EO.c3, 1, 1)                                                                                AS marca,
       MID(EO.c3, 3, 1)                                                                                AS separado,
       MID(EO.c3, 4, 1)                                                                                AS zonaCarga,
       MID(EO.c3, 5, 8) * 1                                                                            AS entrega,
       CAST(P.date AS DATE)                                                                            AS data,
       CAST(IF(EO.dataEntrega = 0, NULL, EO.dataEntrega) AS DATE)                                      AS dataEntrega,
       EO.pdvno                                                                                        AS pdvno,
       SEC_TO_TIME(P.time)                                                                             AS hora,
       T2.pdvno                                                                                        AS pdvnoVenda,
       IFNULL(CAST(T2.nfno_venda AS CHAR), '')                                                         AS nfnoFat,
       IFNULL(T2.nfse_venda, '')                                                                       AS nfseFat,
       IF(T2.data_venda = 0, NULL, CAST(T2.data_venda AS DATE))                                        AS dataFat,
       SEC_TO_TIME(nff2.auxLong4)                                                                      AS horaFat,
       (valor_venda / 100)                                                                             AS valorFat,
       IFNULL(CAST(T2.nfno_entrega AS CHAR), '')                                                       AS nfnoEnt,
       IFNULL(T2.nfse_entrega, '')                                                                     AS nfseEnt,
       IF(T2.data_entrega = 0, NULL, CAST(T2.data_entrega AS DATE))                                    AS dataEnt,
       SEC_TO_TIME(nfe2.auxLong4)                                                                      AS horaEnt,
       (valor_entrega / 100)                                                                           AS valorEnt,
       IFNULL(E.no, 0)                                                                                 AS vendno,
       CAST(CONCAT(E.no, '-', E.name) AS CHAR)                                                         AS vendedor,
       IFNULL(C.no, 0)                                                                                 AS custno,
       CAST(CONCAT(LPAD(C.no * 1, 6, '0'), '-', C.name) AS CHAR)                                       AS cliente,
       CAST(CONCAT('(', IFNULL(CA.ddd, LEFT(C.ddd, 3)), ')', IFNULL(CA.tel, LEFT(C.tel, 10))) AS CHAR) AS foneCliente,
       CAST(RPAD(IFNULL(CA.city, C.city1), 20, ' ') AS CHAR)                                           AS cidade,
       CAST(IFNULL(CA.state, C.state1) AS CHAR)                                                        AS estado,
       C.add1                                                                                          AS endereco,
       C.nei1                                                                                          AS bairro,
       CAST(RPAD(IFNULL(CA.addr, C.add1), 60, ' ') AS CHAR)                                            AS enderecoEntrega,
       RPAD(IFNULL(CA.nei, C.nei1), 25, ' ')                                                           AS bairroEntrega,
       IFNULL(T2.fre_amt, 0) / 100                                                                     AS frete,
       EO.amount / 100                                                                                 AS valor,
       @TIPO                                                                                           AS status,
       IFNULL(A.name, '')                                                                              AS area,
       IFNULL(R.name, '')                                                                              AS rota,
       IF(LEFT(OBS.remarks__480, 2) = 'EF ', LEFT(OBS.remarks__480, 11), ' ')                          AS obs,
       A.no                                                                                            AS codArea,
       EO.userno                                                                                       AS userno,
       IFNULL(U.name, '')                                                                              AS username,
       CAST(IF(EO.l9 = 0, NULL, EO.l9) AS DATE)                                                        AS dataPrint,
       SEC_TO_TIME(IF(EO.l8 = 0, NULL, EO.l8))                                                         AS horaPrint,
       EO.l5                                                                                           AS userPrint,
       RPAD(IFNULL(MID(O.remarks__480, 1, 80), ' '), 80, ' ')                                          AS obs1,
       RPAD(IFNULL(MID(O.remarks__480, 81, 80), ' '), 80, ' ')                                         AS obs2,
       RPAD(IFNULL(MID(O.remarks__480, 161, 80), ' '), 80, ' ')                                        AS obs3,
       RPAD(IFNULL(MID(O.remarks__480, 241, 80), ' '), 80, ' ')                                        AS obs4,
       RPAD(IFNULL(MID(O.remarks__480, 321, 80), ' '), 80, ' ')                                        AS obs5,
       RPAD(IFNULL(MID(O.remarks__480, 401, 80), ' '), 80, ' ')                                        AS obs6,
       RPAD(IFNULL(MID(O.remarks__480, 481, 80), ' '), 80, ' ')                                        AS obs7,
       @TIPO                                                                                           AS tipo,
       paym.name                                                                                       AS metodo,
       nfe.remarks                                                                                     AS obsNota
FROM
  T2
    LEFT JOIN sqldados.eord   AS EO
              ON (T2.storeno = EO.storeno AND T2.ordno = EO.ordno)
    LEFT JOIN sqldados.store  AS S
              ON S.no = EO.storeno
    LEFT JOIN sqldados.eordrk AS O
              ON (O.storeno = EO.storeno AND O.ordno = EO.ordno)
    LEFT JOIN sqldados.ctadd  AS CA
              ON (EO.custno = CA.custno AND CA.seqno = EO.custno_addno)
    LEFT JOIN sqldados.users  AS U
              ON U.no = EO.userno
    LEFT JOIN sqldados.custp  AS C
              ON (C.no = EO.custno)
    LEFT JOIN sqldados.emp    AS E
              ON (E.no = EO.empno)
    LEFT JOIN sqldados.paym
              ON (paym.no = EO.paymno)
    LEFT JOIN sqlpdv.pxa      AS P
              ON (EO.storeno = P.storeno AND EO.ordno = P.eordno AND EO.nfno_futura = P.nfno AND
                  EO.nfse_futura = P.nfse)
    LEFT JOIN sqldados.ctadd  AS AD
              ON (C.no = AD.custno AND AD.seqno = EO.custno_addno)
    LEFT JOIN sqldados.route  AS R
              ON (AD.routeno = R.no)
    LEFT JOIN sqldados.area   AS A
              ON (A.no = R.areano)
    LEFT JOIN sqldados.nf     AS nff
              ON (T2.nfno_venda = nff.nfno AND T2.nfse_venda = nff.nfse AND T2.storeno = nff.storeno)
    LEFT JOIN sqldados.nf2    AS nff2
              ON nff.storeno = nff2.storeno AND nff.pdvno = nff2.pdvno AND nff.xano = nff2.xano
    LEFT JOIN sqldados.nf     AS nfe
              ON (T2.nfno_entrega = nfe.nfno AND T2.nfse_entrega = nfe.nfse AND T2.storeno = nfe.storeno)
    LEFT JOIN sqldados.nf2    AS nfe2
              ON nfe.storeno = nfe2.storeno AND nfe.pdvno = nfe2.pdvno AND nfe.xano = nfe2.xano
    LEFT JOIN sqldados.eordrk AS OBS
              ON (OBS.storeno = EO.storeno AND OBS.ordno = EO.ordno)
WHERE EO.status NOT IN (3, 5)
  AND (EO.date >= @DATA)
  AND (nff.status <> 1 OR nff.status IS NULL)
  AND (P.date >= :dataInicial OR :dataInicial = 0)
  AND (P.date <= :dataFinal OR :dataFinal = 0)
GROUP BY T2.storeno, T2.ordno;

DROP TEMPORARY TABLE IF EXISTS VENDA_ECOMERCE;
CREATE TEMPORARY TABLE VENDA_ECOMERCE
SELECT EO.storeno                                                                                      AS loja,
       EO.storeno                                                                                      AS storenoStk,
       S.name                                                                                          AS nomeLoja,
       S.sname                                                                                         AS siglaLoja,
       EO.ordno                                                                                        AS pedido,
       MID(EO.c3, 1, 1)                                                                                AS marca,
       TRIM(MID(EO.c3, 3, 1))                                                                          AS zonaCarga,
       MID(EO.c3, 4, 1)                                                                                AS separado,
       MID(EO.c3, 5, 8) * 1                                                                            AS entrega,
       CAST(P.date AS DATE)                                                                            AS data,
       CAST(IF(EO.dataEntrega = 0, NULL, EO.dataEntrega) AS DATE)                                      AS dataEntrega,
       EO.pdvno                                                                                        AS pdvno,
       SEC_TO_TIME(P.time)                                                                             AS hora,
       T2.pdvno                                                                                        AS pdvnoVenda,
       IFNULL(CAST(T2.nfno_venda AS CHAR), '')                                                         AS nfnoFat,
       IFNULL(T2.nfse_venda, '')                                                                       AS nfseFat,
       IF(T2.data_venda = 0, NULL, CAST(T2.data_venda AS DATE))                                        AS dataFat,
       SEC_TO_TIME(nff2.auxLong4)                                                                      AS horaFat,
       (valor_venda / 100)                                                                             AS valorFat,
       IFNULL(CAST(T2.nfno_entrega AS CHAR), '')                                                       AS nfnoEnt,
       IFNULL(T2.nfse_entrega, '')                                                                     AS nfseEnt,
       IF(T2.data_entrega = 0, NULL, CAST(T2.data_entrega AS DATE))                                    AS dataEnt,
       SEC_TO_TIME(nfe2.auxLong4)                                                                      AS horaEnt,
       (valor_entrega / 100)                                                                           AS valorEnt,
       IFNULL(E.no, 0)                                                                                 AS vendno,
       CAST(CONCAT(E.no, '-', E.name) AS CHAR)                                                         AS vendedor,
       IFNULL(C.no, 0)                                                                                 AS custno,
       CAST(CONCAT(LPAD(C.no * 1, 6, '0'), '-', C.name) AS CHAR)                                       AS cliente,
       CAST(CONCAT('(', IFNULL(CA.ddd, LEFT(C.ddd, 3)), ')', IFNULL(CA.tel, LEFT(C.tel, 10))) AS CHAR) AS foneCliente,
       CAST(RPAD(IFNULL(CA.city, C.city1), 20, ' ') AS CHAR)                                           AS cidade,
       CAST(IFNULL(CA.state, C.state1) AS CHAR)                                                        AS estado,
       C.add1                                                                                          AS endereco,
       C.nei1                                                                                          AS bairro,
       CAST(RPAD(IFNULL(CA.addr, C.add1), 60, ' ') AS CHAR)                                            AS enderecoEntrega,
       RPAD(IFNULL(CA.nei, C.nei1), 25, ' ')                                                           AS bairroEntrega,
       IFNULL(T2.fre_amt, 0) / 100                                                                     AS frete,
       EO.amount / 100                                                                                 AS valor,
       @TIPO                                                                                           AS status,
       IFNULL(A.name, '')                                                                              AS area,
       IFNULL(R.name, '')                                                                              AS rota,
       IF(LEFT(OBS.remarks__480, 2) = 'EF ', LEFT(OBS.remarks__480, 11), ' ')                          AS obs,
       A.no                                                                                            AS codArea,
       EO.userno                                                                                       AS userno,
       IFNULL(U.name, '')                                                                              AS username,
       CAST(IF(EO.l9 = 0, NULL, EO.l9) AS DATE)                                                        AS dataPrint,
       SEC_TO_TIME(IF(EO.l8 = 0, NULL, EO.l8))                                                         AS horaPrint,
       EO.l5                                                                                           AS userPrint,
       RPAD(IFNULL(MID(O.remarks__480, 1, 80), ' '), 80, ' ')                                          AS obs1,
       RPAD(IFNULL(MID(O.remarks__480, 81, 80), ' '), 80, ' ')                                         AS obs2,
       RPAD(IFNULL(MID(O.remarks__480, 161, 80), ' '), 80, ' ')                                        AS obs3,
       RPAD(IFNULL(MID(O.remarks__480, 241, 80), ' '), 80, ' ')                                        AS obs4,
       RPAD(IFNULL(MID(O.remarks__480, 321, 80), ' '), 80, ' ')                                        AS obs5,
       RPAD(IFNULL(MID(O.remarks__480, 401, 80), ' '), 80, ' ')                                        AS obs6,
       RPAD(IFNULL(MID(O.remarks__480, 481, 80), ' '), 80, ' ')                                        AS obs7,
       @TIPO                                                                                           AS tipo,
       paym.name                                                                                       AS metodo,
       nff.remarks                                                                                     AS obsNota
FROM
  sqldados.eord                AS EO
    INNER JOIN T2_ECOMERCE     AS T2
               ON (T2.storeno = EO.storeno AND T2.ordno = EO.ordno)
    LEFT JOIN  sqldados.store  AS S
               ON S.no = EO.storeno
    LEFT JOIN  sqldados.eordrk AS O
               ON (O.storeno = EO.storeno AND O.ordno = EO.ordno)

    LEFT JOIN  sqldados.ctadd  AS CA
               ON (EO.custno = CA.custno AND CA.seqno = EO.custno_addno)
    LEFT JOIN  sqldados.users  AS U
               ON U.no = EO.userno
    LEFT JOIN  sqldados.custp  AS C
               ON (C.no = EO.custno)
    LEFT JOIN  sqldados.emp    AS E
               ON (E.no = EO.empno)
    LEFT JOIN  sqldados.paym
               ON (paym.no = EO.paymno)
    LEFT JOIN  sqlpdv.pxa      AS P
               ON (EO.storeno = P.storeno AND EO.ordno = P.eordno AND EO.nfno_futura = P.nfno AND
                   EO.nfse_futura = P.nfse)
    LEFT JOIN  sqldados.ctadd  AS AD
               ON (C.no = AD.custno AND AD.seqno = EO.custno_addno)
    LEFT JOIN  sqldados.route  AS R
               ON (AD.routeno = R.no)
    LEFT JOIN  sqldados.area   AS A
               ON (A.no = R.areano)
    LEFT JOIN  sqldados.nf     AS nff
               ON (T2.nfno_venda = nff.nfno AND T2.nfse_venda = nff.nfse AND T2.storeno = nff.storeno)
    LEFT JOIN  sqldados.nf2    AS nff2
               ON nff.storeno = nff2.storeno AND nff.pdvno = nff2.pdvno AND nff.xano = nff2.xano
    LEFT JOIN  sqldados.nf     AS nfe
               ON (T2.nfno_entrega = nfe.nfno AND T2.nfse_entrega = nfe.nfse AND T2.storeno = nfe.storeno)
    LEFT JOIN  sqldados.nf2    AS nfe2
               ON nfe.storeno = nfe2.storeno AND nfe.pdvno = nfe2.pdvno AND nfe.xano = nfe2.xano
    LEFT JOIN  sqldados.eordrk AS OBS
               ON (OBS.storeno = EO.storeno AND OBS.ordno = EO.ordno)
WHERE EO.status NOT IN (0, 5)
  AND (EO.date >= @DATA)
  AND (nff.status <> 1 OR nff.status IS NULL)
  AND (EO.date >= :dataInicial OR :dataInicial = 0)
  AND (EO.date <= :dataFinal OR :dataFinal = 0)
GROUP BY EO.storeno, EO.ordno
HAVING (enderecoEntrega LIKE '%MAGALHAES FILHO%2001%' AND @TIPO = 'R')
    OR (enderecoEntrega NOT LIKE '%MAGALHAES FILHO%2001%' AND @TIPO = 'E');

DROP TEMPORARY TABLE IF EXISTS PEDIDOS;
CREATE TEMPORARY TABLE PEDIDOS
SELECT loja,
       storenoStk,
       nomeLoja,
       siglaLoja,
       pedido,
       separado,
       zonaCarga,
       entrega,
       marca,
       data,
       dataEntrega,
       pdvno,
       hora,
       pdvnoVenda,
       nfnoFat,
       nfseFat,
       dataFat,
       horaFat,
       valorFat,
       nfnoEnt,
       nfseEnt,
       dataEnt,
       horaEnt,
       valorEnt,
       vendno,
       vendedor,
       custno,
       cliente,
       foneCliente,
       cidade,
       estado,
       endereco,
       bairro,
       enderecoEntrega,
       bairroEntrega,
       frete,
       valor,
       status,
       area,
       rota,
       obs,
       codArea,
       userno,
       username,
       dataPrint,
       horaPrint,
       userPrint,
       obs1,
       obs2,
       obs3,
       obs4,
       obs5,
       obs6,
       obs7,
       tipo,
       metodo,
       obsNota,
       'RETIRA_FUTURA' AS tipoRetira
FROM
  VENDA_NORMAL AS VN
UNION
SELECT loja,
       storenoStk,
       nomeLoja,
       siglaLoja,
       pedido,
       separado,
       zonaCarga,
       entrega,
       marca,
       data,
       dataEntrega,
       pdvno,
       hora,
       pdvnoVenda,
       nfnoFat,
       nfseFat,
       dataFat,
       horaFat,
       valorFat,
       nfnoEnt,
       nfseEnt,
       dataEnt,
       horaEnt,
       valorEnt,
       vendno,
       vendedor,
       custno,
       cliente,
       foneCliente,
       cidade,
       estado,
       endereco,
       bairro,
       enderecoEntrega,
       bairroEntrega,
       frete,
       valor,
       status,
       area,
       rota,
       obs,
       codArea,
       userno,
       username,
       dataPrint,
       horaPrint,
       userPrint,
       obs1,
       obs2,
       obs3,
       obs4,
       obs5,
       obs6,
       obs7,
       tipo,
       metodo,
       obsNota,
       'RETIRA_FUTURA_L' AS tipoRetira
FROM
  VENDA_CARGA AS VC
UNION
SELECT loja,
       storenoStk,
       nomeLoja,
       siglaLoja,
       pedido,
       separado,
       zonaCarga,
       entrega,
       marca,
       data,
       dataEntrega,
       pdvno,
       hora,
       pdvnoVenda,
       nfnoFat,
       nfseFat,
       dataFat,
       horaFat,
       valorFat,
       nfnoEnt,
       nfseEnt,
       dataEnt,
       horaEnt,
       valorEnt,
       vendno,
       vendedor,
       custno,
       cliente,
       foneCliente,
       cidade,
       estado,
       endereco,
       bairro,
       enderecoEntrega,
       bairroEntrega,
       frete,
       valor,
       status,
       area,
       rota,
       obs,
       codArea,
       userno,
       username,
       dataPrint,
       horaPrint,
       userPrint,
       obs1,
       obs2,
       obs3,
       obs4,
       obs5,
       obs6,
       obs7,
       tipo,
       metodo,
       obsNota,
       'RETIRA_WEB' AS tipoRetira
FROM
  VENDA_ECOMERCE;

DROP TEMPORARY TABLE IF EXISTS PEDIDO_PISO;
CREATE TEMPORARY TABLE PEDIDO_PISO
(
  PRIMARY KEY (loja, pedido)
)
SELECT P.storeno AS loja, P.ordno AS pedido, SUM(qtty / 1000) AS piso
FROM
  sqldados.eoprd       AS   P
    INNER JOIN PEDIDOS AS   E
               ON P.storeno = E.loja AND P.ordno = E.pedido
    INNER JOIN sqldados.prd PR
               ON PR.no = P.prdno AND PR.groupno = 10000
GROUP BY P.storeno, P.ordno;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno)
)
SELECT prdno,
       MID(MAX(CONCAT(IF(L.localizacao LIKE 'CD%', 2, 1), LPAD(MID(L.localizacao, 1, 3), 3, ' '),
                      LPAD(255 - ASCII(MID(L.localizacao, 4, 1)), 4, '0'), RPAD(MID(L.localizacao, 1, 4), 4, ' '))), 9,
           4) AS localizacao
FROM
  sqldados.prdloc AS L
WHERE storeno = 4
GROUP BY prdno;

DROP TEMPORARY TABLE IF EXISTS PEDIDO_CD;
CREATE TEMPORARY TABLE PEDIDO_CD
(
  PRIMARY KEY (loja, pedido)
)
SELECT P.storeno AS loja,
       P.ordno   AS pedido,
       MID(MAX(CONCAT(IF(L.localizacao LIKE 'CD%', 2, 1), LPAD(MID(L.localizacao, 1, 3), 3, ' '),
                      LPAD(255 - ASCII(MID(L.localizacao, 4, 1)), 4, '0'), RPAD(MID(L.localizacao, 1, 4), 4, ' '))), 9,
           4)    AS loc
FROM
  sqldados.eoprd       AS P
    INNER JOIN PEDIDOS AS E
               ON P.storeno = E.loja AND P.ordno = E.pedido
    LEFT JOIN  T_LOC   AS L
               ON P.prdno = L.prdno
GROUP BY P.storeno, P.ordno;

SELECT loja,
       storenoStk,
       nomeLoja,
       siglaLoja,
       pedido,
       separado,
       zonaCarga,
       DATE(IF(entrega = 0, NULL, entrega))     AS entrega,
       marca,
       data,
       dataEntrega,
       pdvno,
       hora,
       pdvnoVenda,
       nfnoFat,
       nfseFat,
       dataFat,
       horaFat,
       valorFat,
       nfnoEnt,
       nfseEnt,
       dataEnt,
       horaEnt,
       valorEnt,
       vendno,
       vendedor,
       custno,
       cliente,
       foneCliente,
       cidade,
       estado,
       endereco,
       bairro,
       enderecoEntrega,
       bairroEntrega,
       frete,
       valor,
       status,
       area,
       rota,
       obs,
       codArea,
       userno,
       username,
       dataPrint,
       horaPrint,
       userPrint,
       U.name                                   AS userPrintName,
       obs1,
       obs2,
       obs3,
       obs4,
       obs5,
       obs6,
       obs7,
       tipo,
       metodo,
       IFNULL(piso, 0.00)                       AS piso,
       IFNULL(loc, '')                          AS loc,
       obsNota,
       IF(vendno = 440 AND loja = 4, 'WEB', '') AS tipoEcommece,
       tipoRetira                               AS tipoRetira
FROM
  PEDIDOS
    LEFT JOIN PEDIDO_PISO
              USING (loja, pedido)
    LEFT JOIN PEDIDO_CD
              USING (loja, pedido)
    LEFT JOIN sqldados.users AS U
              ON userPrint = U.no
WHERE tipoRetira = :tipoRetira
   OR :tipoRetira = 'TODOS'
HAVING (@PESQUISA = '' OR tipoEcommece = @PESQUISA OR loja = @PESQUISANUM OR pedido = @PESQUISANUM OR
        nfnoFat = @PESQUISANUM OR vendno = @PESQUISANUM OR cliente LIKE @PESQUISALIKE)
