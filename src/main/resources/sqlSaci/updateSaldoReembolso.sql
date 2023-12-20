USE sqldados;

DO @invno := :invno;
DO @custno := :custno;
DO @saldo := :saldo;

DROP TEMPORARY TABLE IF EXISTS T_SALDO;
CREATE TEMPORARY TABLE T_SALDO
SELECT invno, custnoLoj, custnoDev, saldo
FROM sqldados.saldoDevolucao
WHERE invno = @invno;

UPDATE sqldados.custp AS C
  INNER JOIN T_SALDO AS S
  ON C.no = S.custnoLoj
SET C.saldoDevolucao = C.saldoDevolucao + S.saldo
WHERE C.no = S.custnoLoj;

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao - @saldo
WHERE C.no = @custno;

REPLACE INTO sqldados.saldoDevolucao(invno, custnoLoj, custnoDev, saldo)
SELECT @invno AS invno, IFNULL(@custno, 0) AS custnoLoj, IFNULL(@custno, 0) AS custnoDev, @saldo AS saldo
FROM DUAL

