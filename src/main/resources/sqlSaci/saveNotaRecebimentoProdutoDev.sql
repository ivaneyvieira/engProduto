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


REPLACE sqldados.iprdAdicionalDev(invno, prdno, grade, numero, tipoDevolucao, quantDevolucao)
  VALUE (@INVNO, :prdno, :grade, :numero, :tipoDevolucao, :quantDevolucao);

UPDATE sqldados.iprdAdicionalDev
SET grade = :gradeNova
WHERE invno = @INVNO
  AND prdno = :prdno
  AND grade = :grade
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE sqldados.invAdicional
SET situacaoDev = :situacaoDev
WHERE invno = @INVNO
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

REPLACE sqldados.invAdicional(invno, tipoDevolucao, numero, situacaoDev)
VALUES (@INVNO, :tipoDevolucao, :numero, :situacaoDev);

UPDATE sqldados.iprdAdicionalDev
SET invno = :invnoNovo
WHERE invno = @INVNO
  AND prdno = :prdno
  AND grade = :grade
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero;

UPDATE sqldados.invAdicional
SET invno = :invnoNovo
WHERE invno = @INVNO
  AND tipoDevolucao = :tipoDevolucao
  AND numero = :numero