/* "$loja-$ni-$tipoDevolucao-$numeroDevolucao" */

DROP TEMPORARY TABLE IF EXISTS T_NI;
CREATE TEMPORARY TABLE T_NI
SELECT invno, tipoDevolucao, numero
FROM
  sqldados.inv                       AS I
    INNER JOIN sqldados.invAdicional AS A
               USING (invno)
WHERE I.storeno = :loja
  AND A.invno = :ni
  AND A.tipoDevolucao = :tipoDevolucao
  AND A.numero = :numeroDevolucao;

DELETE
FROM
  sqldados.invAdicional
WHERE (invno, tipoDevolucao, numero) IN ( SELECT invno, tipoDevolucao, numero FROM T_NI )