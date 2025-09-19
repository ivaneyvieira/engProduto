USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRDLOC;
CREATE TEMPORARY TABLE T_PRDLOC
SELECT storeno, prdno, grade, localizacao
FROM
  sqldados.prdloc
WHERE localizacao LIKE 'CD3C%'
  AND storeno = 4;

REPLACE INTO prdAdicional (storeno, prdno, grade, estoque, localizacao, dataInicial, dataUpdate, kardec, dataObservacao,
                           observacao, estoqueCD, estoqueLoja, estoqueData, estoqueUser)
SELECT L.storeno,
       L.prdno,
       L.grade,
       IFNULL(A.estoque, 0)     AS estoque,
       L.localizacao,
       IFNULL(A.dataInicial, 0) AS dataInicial,
       A.dataUpdate,
       A.kardec,
       A.dataObservacao,
       A.observacao,
       A.estoqueCD,
       A.estoqueLoja,
       A.estoqueData,
       A.estoqueUser
FROM
  T_PRDLOC                 AS L
    LEFT JOIN prdAdicional AS A
              USING (storeno, prdno, grade)
