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
       0     AS gravadoLogin,
       FALSE AS gravado,
       0     AS movimentacao,
       0     AS estoque,
       0     AS noRota,
       NULL  AS dataEntrege,
       NULL  AS horaEntrege,
       NULL  AS dataRecebido,
       NULL  AS horaRecebido
FROM
  T_ACERTO
