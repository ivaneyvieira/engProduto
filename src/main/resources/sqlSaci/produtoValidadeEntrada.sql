SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISALIKE := IF(@PESQUISA NOT REGEXP '[0-9]+', CONCAT('%', @PESQUISA, '%'), '');
DO @CODIGO := :codigo;
DO @PRDNO := LPAD(@CODIGO, 16, ' ');
DO @VALIDADE := :validade;
DO @GRADE := :grade;
DO @CARACTER := :caracter;

SELECT loja,
       lojaAbrev,
       prdno,
       codigo,
       descricao,
       unidade,
       validade,
       vendno,
       fornecedorAbrev,
       estoqueTotal,
       estoqueLoja,
       grade,
       date,
       mesAno,
       qtty
FROM
  sqldados.produtoEntrada
WHERE date >= :dataInicial
  AND (prdno = @PRDNO OR @CODIGO = '')
  AND (validade = @VALIDADE OR @VALIDADE = 0)
  AND (grade = @GRADE OR @GRADE = '')
  AND CASE :caracter
        WHEN 'S' THEN descricao NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN descricao REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
                 ELSE FALSE
      END
  AND ((:ano = 0) OR (MID(mesAno, 1, 4) = :ano))
  AND ((:mes = 0) OR (MID(mesAno, 5, 6) = :mes))
  AND (@PESQUISA = '' OR descricao LIKE @PESQUISALIKE OR fornecedorAbrev LIKE @PESQUISALIKE OR unidade = @PESQUISA OR
       vendno = @PESQUISANUM)