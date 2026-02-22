USE sqldados;

SET SQL_MODE = '';



SELECT O.storeno                               AS loja,
       O.ordno                                 AS numero,
       IF(fjflag = 1, F.name, F.id_sname)      AS cliente,
       CAST(O.date AS DATE)                    AS data,
       NULL                                    AS localizacao,
       NULL                                    AS observacao,
       IFNULL(EE.no, 0)                        AS entregueNo,
       IFNULL(EE.name, '')                     AS entregueNome,
       IFNULL(EE.login, '')                    AS entregueSNome,
       IFNULL(EF.no, 0)                        AS finalizadoNo,
       IFNULL(EF.name, '')                     AS finalizadoNome,
       IFNULL(EF.login, '')                    AS finalizadoSNome,
       IFNULL(ER.no, 0)                        AS recebidoNo,
       IFNULL(ER.name, '')                     AS recebidoNome,
       IFNULL(ER.sname, '')                    AS recebidoSNome,
       E.prdno                                 AS prdno,
       TRIM(E.prdno)                           AS codigo,
       E.grade                                 AS grade,
       IFNULL(EA.marca, 0)                     AS marca,
       TRIM(IFNULL(B.barcode, P.barcode))      AS barcode,
       TRIM(MID(P.name, 1, 37))                AS descricao,
       ROUND(E.qtty / 1000)                    AS quantidade,
       IFNULL(EA.qtRecebido, 0)                AS qtRecebido,
       EA.selecionado                          AS selecionado,
       EA.posicao                              AS posicao,
       (S.qtty_atacado + S.qtty_varejo) / 1000 AS qtEstoque,
       O.paymno                                AS metodo,
       IF(O.paymno = 433, ROUND(CASE
                                  WHEN R.remarks__480 LIKE 'ENTRADA%' THEN 1
                                  WHEN R.remarks__480 LIKE 'SAIDA%'   THEN -1
                                                                      ELSE 0
                                END), 1)       AS multAcerto
FROM
  sqldados.eoprd                       AS E
    LEFT JOIN  sqldados.eoprdAdicional AS EA
               USING (storeno, ordno, prdno, grade)
    INNER JOIN sqldados.eord           AS O
               USING (storeno, ordno)
    LEFT JOIN  sqldados.eordrk         AS R
               USING (storeno, ordno)

    LEFT JOIN  sqldados.stk            AS S
               ON S.prdno = E.prdno AND S.grade = E.grade AND S.storeno = E.storeno
    LEFT JOIN  sqldados.prdbar         AS B
               ON B.prdno = E.prdno AND B.grade = E.grade AND B.grade != ''
    LEFT JOIN  sqldados.users          AS EE
               ON EE.no = EA.empEntregue
    LEFT JOIN  sqldados.users          AS EF
               ON EF.no = EA.empFinalizado
    LEFT JOIN  sqldados.emp            AS ER
               ON ER.no = EA.empRecebido
    INNER JOIN sqldados.prd            AS P
               ON P.no = E.prdno
    LEFT JOIN  sqldados.custp          AS F
               ON F.no = O.custno
WHERE (O.paymno IN (431, 432, 433))
  AND (O.date >= 20240101)
  AND (O.date >= SUBDATE(CURDATE(), INTERVAL 60 YEAR))
  AND (O.date >= :dataInicial OR :dataInicial = 0)
  AND (O.storeno = :loja OR :loja = 0)
  AND (E.prdno = :prdno OR :prdno = '')
  AND (E.grade = :grade OR :grade = '')
GROUP BY E.storeno, E.ordno, E.prdno, E.grade
HAVING multAcerto != 0
