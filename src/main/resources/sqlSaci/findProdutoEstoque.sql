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

alter table sqldados.prdAdicional
add column dataInicial int default 0

*/

DROP TEMPORARY TABLE IF EXISTS temp_pesquisa;
CREATE TEMPORARY TABLE temp_pesquisa AS
SELECT S.storeno                                                                               AS loja,
       P.no                                                                                    AS prdno,
       TRIM(P.no) * 1                                                                          AS codigo,
       TRIM(MID(P.name, 1, 37))                                                                AS descricao,
       TRIM(MID(P.name, 38, 3))                                                                AS unidade,
       S.grade                                                                                 AS grade,
       ROUND(P.qttyPackClosed / 1000)                                                          AS embalagem,
       TRUNCATE(ROUND((S.qtty_atacado + S.qtty_varejo) / 1000) / (P.qttyPackClosed / 1000), 0) AS qtdEmbalagem,
       IFNULL(A.estoque, 0)                                                                    AS estoque,
       MID(L1.localizacao, 1, 4)                                                               AS locSaci,
       COALESCE(A.localizacao, /*MID(L1.localizacao, 1, 4),*/ '')                              AS locApp,
       V.no                                                                                    AS codForn,
       V.sname                                                                                 AS fornecedor,
       ROUND((S.qtty_atacado + S.qtty_varejo) / 1000)                                          AS saldo,
       CAST(IF(IFNULL(A.dataInicial, 0) = 0, NULL, IFNULL(A.dataInicial, 0)) AS DATE)          AS dataInicial
FROM sqldados.stk AS S
       INNER JOIN sqldados.prd AS P
                  ON S.prdno = P.no
       LEFT JOIN sqldados.vend AS V
                 ON V.no = P.mfno
       LEFT JOIN sqldados.prdAdicional AS A
                 USING (storeno, prdno, grade)
       LEFT JOIN sqldados.prdloc AS L1
                 USING (storeno, prdno, grade)
WHERE (S.storeno = :loja OR :loja = 0)
  AND (TRIM(S.prdno) * 1 = :codigo OR :codigo = 0)
  AND CASE :caracter
        WHEN 'S' THEN P.name NOT REGEXP '^[A-Z0-9]'
        WHEN 'N' THEN P.name REGEXP '^[A-Z0-9]'
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END
  AND (P.mfno = :fornecedor OR V.sname LIKE CONCAT('%', :fornecedor, '%') OR :fornecedor = '')
GROUP BY S.storeno, S.prdno, S.grade, MID(L1.localizacao, 1, 4)
HAVING CASE :estoque
         WHEN '>' THEN saldo > :saldo
         WHEN '<' THEN saldo < :saldo
         WHEN '=' THEN saldo = :saldo
         WHEN 'T' THEN TRUE
         ELSE FALSE
       END;

SELECT loja,
       prdno,
       codigo,
       descricao,
       unidade,
       grade,
       embalagem,
       qtdEmbalagem,
       estoque,
       locSaci,
       locApp,
       codForn,
       fornecedor,
       saldo,
       dataInicial
FROM temp_pesquisa
WHERE (
  @PESQUISA = '' OR
  locSaci LIKE @PESQUISALIKE OR
  codigo = @PESQUISANUM OR
  locSaci LIKE @PESQUISALIKE OR
  descricao LIKE @PESQUISALIKE OR
  unidade LIKE @PESQUISA
  )
  AND (grade LIKE CONCAT(:grade, '%') OR :grade = '')
  AND (locApp LIKE CONCAT(:localizacao, '%') OR :localizacao = '')
  AND (locApp IN (:localizacaoUser) OR locSaci IN (:localizacaoUser) OR 'TODOS' IN (:localizacaoUser))
GROUP BY codigo, grade, locApp
