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
       :login              AS login,
       :usuario            AS usuario,
       ''                  AS prdno,
       ''                  AS descricao,
       ''                  AS grade,
       FALSE               AS processado,
       0                   AS transacao,
       0                   AS estoqueSis,
       0                   AS estoqueCD,
       0                   AS estoqueLoja,
       0                   AS diferenca
FROM
  sqldados.store
WHERE no = :numLoja;

REPLACE INTO produtoEstoqueAcerto (numero, numloja, lojaSigla, data, hora, login, usuario, prdno, descricao, grade,
                                   estoqueSis, estoqueCD, estoqueLoja, diferenca)
SELECT numero,
       numloja,
       lojaSigla,
       data,
       hora,
       login,
       usuario,
       prdno,
       descricao,
       grade,
       estoqueSis,
       estoqueCD,
       estoqueLoja,
       diferenca
FROM
  T_ACERTO;

SELECT numero,
       numloja,
       lojaSigla,
       data,
       hora,
       login,
       usuario,
       prdno,
       descricao,
       grade,
       estoqueSis,
       estoqueCD,
       estoqueLoja,
       diferenca,
       IF(processado, 'Sim', 'Não') AS processado,
       transacao
FROM
  T_ACERTO
