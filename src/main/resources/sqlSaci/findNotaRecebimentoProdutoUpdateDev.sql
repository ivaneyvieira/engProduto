USE sqldados;

SET SQL_MODE = '';

REPLACE sqldados.iprdAdicionalDev(invno, prdno, grade, numero, tipoDevolucao, quantDevolucao)
VALUES (:invno, :prdno, :grade, :numero, :tipoDevolucao, :quantDevolucao)

