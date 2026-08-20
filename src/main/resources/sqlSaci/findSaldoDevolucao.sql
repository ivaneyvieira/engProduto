SELECT no AS codigo, name AS nome, saldoDevolucao / 100 AS saldo
FROM sqldados.custp
WHERE no = :codigo