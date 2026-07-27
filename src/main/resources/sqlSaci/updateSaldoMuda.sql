USE sqldados;

DO @SALDO_ORIGEM = ( SELECT SUM(saldoDevolucao)
                     FROM sqldados.custp
                     WHERE NO = :custnoOri );

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao - ROUND(:saldo * 100.00)
WHERE C.no = :custnoOri
  AND :custnoDes > 0
  AND :custnoOri > 0
  AND @SALDO_ORIGEM >= ROUND(:saldo * 100.00);

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = C.saldoDevolucao + ROUND(:saldo * 100.00)
WHERE C.no = :custnoDes
  AND :custnoOri > 0
  AND :custnoDes > 0
  AND @SALDO_ORIGEM >= ROUND(:saldo * 100.00)
