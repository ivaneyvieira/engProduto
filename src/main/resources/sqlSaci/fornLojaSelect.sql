SET SQL_MODE = '';

SELECT V.no                                                                     AS vendno,
       V.sname                                                                  AS abrev,
       MAX(IF(A.storeno = 2, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataDS,
       MAX(IF(A.storeno = 3, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataMR,
       MAX(IF(A.storeno = 4, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataMF,
       MAX(IF(A.storeno = 5, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataPK,
       MAX(IF(A.storeno = 8, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataTM
FROM
  sqldados.vend                           AS V
    INNER JOIN sqldados.prd               AS P
               ON P.mfno = V.no
    LEFT JOIN  sqldados.vendLojaAdicional AS A
               ON A.vendno = V.no
WHERE (:pesquisa = '' OR V.no LIKE :pesquisa OR V.sname LIKE CONCAT('%', :pesquisa, '%'))
  AND (A.data = :data OR :data = 0)
GROUP BY V.no
ORDER BY V.no
