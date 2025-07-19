SET SQL_MODE = '';

USE sqldados;

DROP TABLE IF EXISTS T_FORN;
CREATE TEMPORARY TABLE T_FORN
SELECT V.no       AS no,
       V.name     AS descricao,
       V.cgc      AS cnpjCpf,
       V.fabOufor AS classe,
       CASE V.fabOufor
         WHEN 0 THEN 'Fabricante'
         WHEN 1 THEN 'Fornecedor'
         WHEN 2 THEN 'Fab/Fornecedor'
                ELSE 'Desconhecido'
       END        AS classificacao
FROM
  sqldados.vend AS V
WHERE V.fabOufor IN (0, 1, 2)
HAVING :pesquisa = ''
    OR no = :pesquisa
    OR descricao LIKE CONCAT('%', :pesquisa, '%')
    OR cnpjCpf = :pesquisa
    OR classificacao LIKE CONCAT('%', :pesquisa, '%')
ORDER BY no;

SELECT no, descricao, cnpjCpf, classe, classificacao
FROM
  T_FORN

