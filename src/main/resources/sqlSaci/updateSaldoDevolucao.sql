use sqldados;

DO @invno := :invno;
DO @custnoDev := :custno;
DO @saldo := :saldo;

DO @custnoLoj := (SELECT MAX(C.no)
FROM sqldados.custp AS C
       INNER JOIN sqldados.store AS S
                  ON C.cpf_cgc = S.cgc
                    AND S.no IN (2, 3, 4, 5, 7, 6, 8)
       INNER JOIN sqldados.custp AS D
                  ON D.no = S.no * 100
WHERE C.cpf_cgc LIKE '07.483.654/%'
  AND S.no*100 = @custnoDev);

/*
select @invno, @custnoDev, @saldo, @custnoLoj
*/

DROP TEMPORARY TABLE IF EXISTS T_SALDO;
CREATE TEMPORARY TABLE T_SALDO
SELECT invno, custnoLoj, custnoDev, saldo
FROM sqldados.saldoDevolucao
WHERE invno = @invno
  AND @custnoDev IN (200, 300, 400, 500, 600, 800);

UPDATE sqldados.custp AS C
  INNER JOIN T_SALDO AS S
  ON C.no = S.custnoLoj
SET C.saldoDevolucao = C.saldoDevolucao + S.saldo
WHERE C.no = S.custnoLoj;

UPDATE sqldados.custp AS C
  INNER JOIN T_SALDO AS S
  ON C.no = S.custnoDev
SET C.saldoDevolucao = C.saldoDevolucao - S.saldo
WHERE C.no = S.custnoDev;

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao - @saldo
WHERE C.no = @custnoLoj
  AND @custnoDev IN (200, 300, 400, 500, 600, 800);

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao + @saldo
WHERE C.no = @custnoDev
  AND @custnoDev IN (200, 300, 400, 500, 600, 800);

REPLACE INTO sqldados.saldoDevolucao(invno, custnoLoj, custnoDev, saldo)
SELECT @invno AS invno, IFNULL(@custnoLoj, 0) AS custnoLoj, IFNULL(@custnoDev, 0) AS custnoDev, @saldo AS saldo
FROM DUAL
WHERE @custnoDev IN (200, 300, 400, 500, 600, 800)
