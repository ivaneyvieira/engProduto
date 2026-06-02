USE sqldados;

/*Kardec CD */

DROP
  TEMPORARY TABLE IF EXISTS T_PRD_DEV;
CREATE
  TEMPORARY TABLE T_PRD_DEV
(
  INDEX (loja, prdno, grade)
)
SELECT N.storeno           AS loja,
       A.prdno             AS prdno,
       A.grade             AS grade,
       N.date              AS date,
       SUM(quantDevolucao) AS quantDevolucao
FROM
  sqldados.iprdAdicionalDev          AS A
    LEFT JOIN  sqldados.invAdicional AS IA
               USING (invno, tipoDevolucao, numero)
    INNER JOIN sqldados.inv          AS N
               USING (invno)
WHERE (situacaoDev = 0 OR situacaoDev IS NULL)
  AND tipoDevolucao = 8
  AND (N.storeno = :loja OR :loja = 0)
  AND (prdno = :prdno)
  AND (grade = :grade)
GROUP BY N.storeno, A.prdno, A.grade, N.date;

SELECT loja,
       prdno,
       grade,
       data,
       ljDoc,
       doc,
       pedido,
       nfEnt,
       tipo,
       CAST(IF(vencimento * 1 = 0, NULL, vencimento * 1) AS DATE) AS vencimento,
       qtde,
       saldo,
       userLogin,
       recLogin,
       entLogin,
       observacao,
       IFNULL(quantDevolucao, 0)                                  AS quantDevolucao
FROM
  sqldados.produtoKardec                      AS K
    LEFT JOIN ( SELECT loja,
                       prdno,
                       grade,
                       SUM(quantDevolucao) AS quantDevolucao
                FROM
                  T_PRD_DEV AS D
                WHERE D.date <= CURRENT_DATE * 1
                GROUP BY loja, prdno, grade ) AS DEV
              USING (loja, prdno, grade)
WHERE (loja = :loja OR :loja = 0)
  AND prdno = :prdno
  AND grade = :grade
  AND data <= CURRENT_DATE * 1