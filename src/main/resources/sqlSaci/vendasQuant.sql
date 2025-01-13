USE sqldados;

SELECT N.storeno                 AS loja,
       N.pdvno                   AS pdv,
       N.xano                    AS transacao,
       X.prdno                   AS prdno,
       X.grade                   AS grade,
       CAST(N.issuedate AS DATE) AS data,
       ROUND(X.qtty / 1000)      AS quantidade
FROM sqldados.nf AS N
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
WHERE (N.storeno IN (2, 3, 4, 5, 8))
  AND (N.storeno = :loja)
  AND (X.prdno = :prdno)
  AND (X.grade = :grade)
  AND (N.issuedate >= :dataInicial)
  AND N.tipo IN (0, 4)
  AND N.status <> 1

