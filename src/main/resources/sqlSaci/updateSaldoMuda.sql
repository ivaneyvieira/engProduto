USE sqldados;

DO @invno := :invno;
DO @custnoCred := :custnoCred;
DO @saldo := :saldo;
DO @custno := :custno;

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao - @saldo
WHERE C.no = @custno;

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao + @saldo
WHERE C.no = @custnoCred;

REPLACE INTO sqldados.saldoDevolucao(invno, custnoLoj, custnoDev, saldo)
SELECT @invno AS invno, IFNULL(@custno, 0) AS custnoLoj, IFNULL(@custnoCred, 0) AS custnoDev, @saldo AS saldo
FROM DUAL
