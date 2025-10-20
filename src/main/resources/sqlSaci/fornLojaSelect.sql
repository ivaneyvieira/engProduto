SET SQL_MODE = '';

SELECT V.no                                                                     AS vendno,
       V.sname                                                                  AS abrev,
       MAX(IF(A.storeno = 2, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataDS,
       MAX(IF(A.storeno = 2, U.no, NULL))                                       AS usernoDS,
       MAX(IF(A.storeno = 2, U.login, NULL))                                    AS loginDS,
       MAX(IF(A.storeno = 3, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataMR,
       MAX(IF(A.storeno = 3, U.no, NULL))                                       AS usernoMR,
       MAX(IF(A.storeno = 3, U.login, NULL))                                    AS loginMR,
       MAX(IF(A.storeno = 4, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataMF,
       MAX(IF(A.storeno = 4, U.no, NULL))                                       AS usernoMF,
       MAX(IF(A.storeno = 4, U.login, NULL))                                    AS loginMF,
       MAX(IF(A.storeno = 5, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataPK,
       MAX(IF(A.storeno = 5, U.no, NULL))                                       AS usernoPK,
       MAX(IF(A.storeno = 5, U.login, NULL))                                    AS loginPK,
       MAX(IF(A.storeno = 8, CAST(IF(A.data = 0, NULL, A.data) AS date), NULL)) AS dataTM,
       MAX(IF(A.storeno = 8, U.no, NULL))                                       AS usernoTM,
       MAX(IF(A.storeno = 8, U.login, NULL))                                    AS loginTM
FROM
  sqldados.vend                           AS V
    INNER JOIN sqldados.prd               AS P
               ON P.mfno = V.no
    INNER JOIN sqldados.stk               AS S
               ON S.prdno = P.no AND storeno IN (2, 3, 4, 5, 8) and (qtty_varejo != 0 or qtty_atacado != 0)
    LEFT JOIN  sqldados.vendLojaAdicional AS A
               ON A.vendno = V.no
    LEFT JOIN  sqldados.users             AS U
               ON U.no = A.userno
WHERE (:pesquisa = '' OR V.no LIKE :pesquisa OR V.sname LIKE CONCAT('%', :pesquisa, '%'))
  AND (A.data >= :dataInicial OR :dataInicial = 0)
  AND (A.data <= :dataFinal OR :dataFinal = 0)
  AND (P.no < '          960001')
GROUP BY V.no
ORDER BY V.no
