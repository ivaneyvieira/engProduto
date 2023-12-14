USE sqldados;

SET sql_mode = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

SELECT no                             AS codigo,
       name                           AS nome,
       ROUND(saldoDevolucao / 100, 2) AS vlCredito
FROM sqldados.custp AS C
WHERE saldoDevolucao != 0
  AND (@PESQUISA = '' OR no = @PESQUISANUM OR name LIKE @PESQUISALIKE)