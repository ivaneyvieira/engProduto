SELECT E.storeno                                                        AS loja,
       E.ordno                                                          AS pedido,
       CAST(EO.date AS DATE)                                            AS data,
       CAST(IF(EO.nfno = 0, '', CONCAT(EO.nfno, '/', EO.nfse)) AS CHAR) AS nota,
       IF(E.bits & POW(2, 1), 'RETIRA', 'ENTREGA')                      AS tipo,
       EO.custno                                                        AS cliente,
       P.mfno                                                           AS vendno,
       CAST(TRIM(P.no) AS CHAR)                                         AS codigo,
       EO.empno                                                         AS empno,
       TRIM(MID(P.name, 1, 37))                                         AS descricao,
       E.grade                                                          AS grade,
       E.qtty / 1000                                                    AS quant,
       IFNULL(S.qtty_varejo / 1000, 0)                                  AS estSaci,
       IFNULL(S.qtty_varejo / 1000, 0) - E.qtty / 1000                  AS saldo,
       P.typeno                                                         AS typeno,
       IFNULL(T.name, '')                                               AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                               AS clno,
       IFNULL(cl.name, '')                                              AS clname
FROM sqldados.eoprdf       AS E
  INNER JOIN sqldados.eord AS EO
	       USING (storeno, ordno)
  LEFT JOIN  sqldados.stk  AS S
	       USING (storeno, prdno, grade)
  LEFT JOIN  sqldados.prd  AS P
	       ON E.prdno = P.no
  LEFT JOIN  sqldados.vend AS F
	       ON F.no = P.mfno
  LEFT JOIN  sqldados.type AS T
	       ON T.no = P.typeno
  LEFT JOIN  sqldados.cl
	       ON cl.no = P.clno
WHERE (E.prdno = :prdno OR :prdno = '')
  AND (P.typeno = :typeno OR :typeno = 0)
  AND (P.clno = :clno OR P.deptno = :clno OR P.groupno = :clno OR :clno = 0)
  AND (P.mfno = :vendno OR :vendno = 0)
  AND (EO.nfno = :nfno OR :nfno = 0)
  AND (EO.nfse = :nfse OR :nfse = '')
  AND E.date >= 20201001
  AND E.nfNfno = 0
GROUP BY codigo, grade

