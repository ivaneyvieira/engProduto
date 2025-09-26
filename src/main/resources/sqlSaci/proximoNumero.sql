USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_NUMERO;
CREATE TEMPORARY TABLE T_NUMERO
SELECT :nome, numero
FROM
  sqldados.appNumeracao
WHERE nome = :nome;

DROP TEMPORARY TABLE IF EXISTS T_NUMERO_PROX;
CREATE TEMPORARY TABLE T_NUMERO_PROX
SELECT :nome AS nome, IFNULL(( SELECT MAX(numero) FROM T_NUMERO ), 0) + 1 AS numero
FROM
  dual;

REPLACE INTO sqldados.appNumeracao (nome, numero)
SELECT nome, numero
FROM
  T_NUMERO_PROX;

select nome, numero
FROM
  sqldados.appNumeracao
WHERE nome = :nome;