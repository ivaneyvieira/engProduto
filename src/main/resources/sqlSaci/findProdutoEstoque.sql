USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

/*
create table sqldados.prdAdicional (
  storeno int,
  prdno varchar(16),
  grade varchar(10),
  estoque int,
  PRIMARY KEY (storeno, prdno, grade)
)
*/

DROP TEMPORARY TABLE IF EXISTS temp_pesquisa;
CREATE TEMPORARY TABLE temp_pesquisa AS
SELECT S.storeno                                                     AS loja,
       P.no                                                          AS prdno,
       TRIM(P.no) * 1                                                AS codigo,
       TRIM(MID(P.name, 1, 37))                                      AS descricao,
       TRIM(MID(P.name, 38, 3))                                      AS unidade,
       S.grade                                                       AS grade,
       P.qttyPackClosed / 1000                                       AS embalagem,
       TRUNCATE(IFNULL(A.estoque, 0) / (P.qttyPackClosed / 1000), 0) AS qtdEmbalagem,
       IFNULL(A.estoque, 0)                                          AS estoque
FROM sqldados.prd AS P
       INNER JOIN sqldados.stk AS S
                  ON S.prdno = P.no
       LEFT JOIN sqldados.prdAdicional AS A
                 USING (storeno, prdno, grade)
WHERE (S.storeno = :loja OR :loja = 0);

SELECT *
FROM temp_pesquisa
WHERE (
        @PESQUISA = '' OR
        codigo = @PESQUISANUM OR
        descricao LIKE @PESQUISALIKE OR
        unidade LIKE @PESQUISA OR
        grade LIKE @PESQUISALIKE
        )