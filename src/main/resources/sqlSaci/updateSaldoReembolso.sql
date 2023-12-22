USE sqldados;

DO @invno := :invno;
DO @custno := :custno;
DO @saldo := :saldo;

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao - @saldo
WHERE C.no = @custno
  AND @custno > 0;

REPLACE INTO sqldados.saldoDevolucao(invno, custnoLoj, custnoDev, saldo)
SELECT @invno AS invno, IFNULL(@custno, 0) AS custnoLoj, IFNULL(@custno, 0) AS custnoDev, @saldo AS saldo
FROM DUAL
WHERE @custno > 0
