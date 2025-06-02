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
  VALUE (@INVNO, :prdno, :grade, :numero, :tipoDevolucao, :quantDevolucao, :seq);

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, situacaoDev)
VALUES (@INVNO, :tipoDevolucao, :numero, :situacaoDev)
