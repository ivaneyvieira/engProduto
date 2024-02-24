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

alter table sqldados.prdAdicional
add column localizacao varchar(4)

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
       IFNULL(A.estoque, 0)                                          AS estoque,
       COALESCE(A.localizacao, MID(L1.localizacao, 1, 4), '')        AS localizacao,
       ROUND((S.qtty_atacado + S.qtty_varejo) / 1000)                AS saldo
FROM sqldados.stk AS S
       INNER JOIN sqldados.prd AS P
                  ON S.prdno = P.no
       LEFT JOIN sqldados.prdAdicional AS A
                 USING (storeno, prdno, grade)
       LEFT JOIN sqldados.prdloc AS L1
                 USING (storeno, prdno, grade)
WHERE (S.storeno = :loja OR :loja = 0)
  AND CASE :caracter
        WHEN 'S' THEN P.name NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN P.name REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END
GROUP BY S.storeno, S.prdno, S.grade;

SELECT *
FROM temp_pesquisa
WHERE (
  @PESQUISA = '' OR
  codigo = @PESQUISANUM OR
  descricao LIKE @PESQUISALIKE OR
  unidade LIKE @PESQUISA
  )
  AND (grade LIKE CONCAT(:grade, '%') OR :grade = '')
  AND (localizacao LIKE CONCAT(:localizacao, '%') OR :localizacao = '')