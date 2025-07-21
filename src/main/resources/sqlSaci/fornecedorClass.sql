SET SQL_MODE = '';

USE sqldados;

DROP TABLE IF EXISTS T_FORN;
CREATE TEMPORARY TABLE T_FORN
SELECT V.no                  AS no,
       C.no                  AS custno,
       V.name                AS descricao,
       V.cgc                 AS cnpjCpf,
       V.fabOufor            AS classe,
       CASE V.fabOufor
         WHEN 0 THEN 'Fabricante'
         WHEN 1 THEN 'Fornecedor'
         WHEN 2 THEN 'Fab/Fornecedor'
                ELSE 'Desconhecido'
       END                   AS classificacao,
       A.termDev             AS termDev,
       COUNT(DISTINCT F.seq) AS countArq
FROM
  sqldados.vend                    AS V
    LEFT JOIN sqldados.custp       AS C
              ON C.cpf_cgc = V.cgc
    LEFT JOIN vendAdicional        AS A
              ON A.vendno = V.no
    LEFT JOIN sqldados.vendArquivo AS F
              ON F.vendno = V.no
WHERE V.fabOufor IN (0, 1, 2)
GROUP BY V.no, V.name, V.cgc
HAVING :pesquisa = ''
    OR no = :pesquisa
    OR V.name LIKE CONCAT('%', :pesquisa, '%')
    OR V.cgc = :pesquisa
    OR classificacao LIKE CONCAT('%', :pesquisa, '%')
ORDER BY V.no;

SELECT no, custno, descricao, cnpjCpf, classe, classificacao, termDev, countArq
FROM
  T_FORN

