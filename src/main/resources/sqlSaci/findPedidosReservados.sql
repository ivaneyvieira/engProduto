SELECT E.storeno                                       AS loja,
       E.ordno                                         AS pedido,
       CAST(O.date AS DATE)                            AS data,
       O.custno                                        AS cliente,
       P.mfno                                          AS vendno,
       CAST(TRIM(P.no) AS CHAR)                        AS codigo,
       O.empno                                         AS empno,
       TRIM(MID(P.name, 1, 37))                        AS descricao,
       E.grade                                         AS grade,
       E.qtty / 1000                                   AS reserva,
       IFNULL(S.qtty_varejo / 1000, 0)                 AS estSaci,
       IFNULL(S.qtty_varejo / 1000, 0) - E.qtty / 1000 AS saldo,
       P.typeno                                        AS typeno,
       IFNULL(T.name, '')                              AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)              AS clno,
       IFNULL(cl.name, '')                             AS clname,
       MID(IFNULL(L.localizacao, ''), 1, 4)            AS localizacao
FROM sqldados.eord AS O
         INNER JOIN sqldados.eoprd AS E
                    USING (storeno, ordno)
         INNER JOIN sqldados.prd AS P
                    ON P.no = E.prdno
         LEFT JOIN sqldados.vend AS F
                   ON F.no = P.mfno
         LEFT JOIN sqldados.type AS T
                   ON T.no = P.typeno
         LEFT JOIN sqldados.cl
                   ON cl.no = P.clno
         LEFT JOIN sqldados.stk AS S
                   ON E.prdno = S.prdno AND E.grade = S.grade AND S.storeno = E.storeno
         LEFT JOIN sqldados.prdloc AS L
                   ON L.prdno = P.no AND L.grade = E.grade AND L.storeno = 4
WHERE O.status NOT IN (4, 5)
  AND E.status IN (2)
  AND O.storeno IN (2, 3, 4, 5)
  AND (P.no = :prdno OR :prdno = '')
  AND (P.typeno = :typeno OR :typeno = 0)
  AND (P.clno = :clno OR P.deptno = :clno OR P.groupno = :clno OR :clno = 0)
  AND (P.mfno = :vendno OR :vendno = 0)
  AND (L.localizacao LIKE CONCAT(:localizacao, '%') OR :localizacao = '')
  AND (E.storeno = :loja OR :loja = 0)
GROUP BY E.storeno, E.ordno, E.prdno, E.grade