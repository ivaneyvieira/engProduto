USE sqldados;

CREATE TEMPORARY TABLE T_ACERTO
SELECT :numero             AS numero,
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
       processado,
       transacao,
       0                            AS gravadoLogin,
       FALSE                        AS gravado
FROM
  T_ACERTO
