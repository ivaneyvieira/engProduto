DO @INVNO := IF(:invno = 0,
                IFNULL(( SELECT MAX(invno) AS invno
                         FROM
                           sqldados.iprd             AS I
                             INNER JOIN sqldados.inv AS N
                                        USING (invno)
                         WHERE N.type = 0
                           AND N.bits & POW(2, 4) = 0
                           AND I.cfop NOT IN (1910, 2910, 1916, 2916)
                           AND I.prdno = :prdno
                           AND I.grade = :grade
                           AND :invno = 0
                         GROUP BY prdno, grade ), :invno), :invno);

REPLACE sqldados.iprdAdicionalDev(invno, prdno, grade, numero, tipoDevolucao, quantDevolucao, seq)
SELECT @INVNO, :prdno, :grade, :numero, :tipoDevolucao, :quantDevolucao, :seq
FROM
  DUAL
WHERE @INVNO != 0;

REPLACE sqldados.invAdicional (invno, tipoDevolucao, numero, situacaoDev)
SELECT @INVNO, :tipoDevolucao, :numero, :situacaoDev
FROM
  DUAL
WHERE @INVNO != 0;

SELECT @INVNO AS quant
FROM
  DUAL
