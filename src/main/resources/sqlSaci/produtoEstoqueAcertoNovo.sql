USE sqldados;

DELETE
FROM
  produtoEstoqueAcerto
WHERE usuario = :usuario
  AND numloja = :numLoja
  AND descricao = '';

DO @NUMERO := IFNULL(( SELECT MAX(numero)
                       FROM
                         produtoEstoqueAcerto
                       WHERE numloja = :numLoja ), 0);

CREATE TEMPORARY TABLE T_ACERTO
SELECT @NUMERO + 1         AS numero,
       no                  AS numloja,
       sname               AS lojaSigla,
       CAST(:data AS date) AS data,
       SEC_TO_TIME(:hora)  AS hora,
       :usuario            AS usuario,
       ''                  AS prdno,
       ''                  AS descricao,
       ''                  AS grade,
       0                   AS diferenca
FROM
  sqldados.store
WHERE no = :numLoja;

REPLACE INTO produtoEstoqueAcerto (numero, numloja, lojaSigla, data, hora, usuario, prdno, descricao, grade,
                                   diferenca)
SELECT numero, numloja, lojaSigla, data, hora, usuario, prdno, descricao, grade, diferenca
FROM
  T_ACERTO;

SELECT *
FROM
  T_ACERTO
