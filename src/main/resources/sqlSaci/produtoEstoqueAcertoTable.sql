USE sqldados;

DROP TABLE IF EXISTS produtoEstoqueAcerto;
CREATE TABLE produtoEstoqueAcerto
(
  numero    INT          NOT NULL,
  numloja   INT          NOT NULL,
  lojaSigla VARCHAR(2)   NOT NULL,
  data      DATE         NOT NULL,
  hora      TIME         NOT NULL,
  usuario   VARCHAR(50)  NOT NULL,
  prdno     VARCHAR(16)  NOT NULL,
  descricao VARCHAR(100) NOT NULL,
  grade     VARCHAR(20)  NOT NULL,
  diferenca INT          NOT NULL,
  PRIMARY KEY (numloja, numero, prdno, grade)
);

ALTER TABLE produtoEstoqueAcerto
  ADD processado boolean DEFAULT FALSE;

ALTER TABLE produtoEstoqueAcerto
  ADD transacao varchar(20) DEFAULT '';

ALTER TABLE produtoEstoqueAcerto
  ADD login varchar(20) DEFAULT '';

ALTER TABLE produtoEstoqueAcerto
  ADD estoqueCD   int NULL AFTER grade,
  ADD estoqueLoja int NULL AFTER grade;

ALTER TABLE produtoEstoqueAcerto
  ADD estoqueSis int NULL AFTER grade;


SELECT A.*, U.login
FROM
  produtoEstoqueAcerto AS A
    INNER JOIN users   AS U
               ON U.name = A.usuario;


UPDATE produtoEstoqueAcerto AS A INNER JOIN users AS U ON U.name = A.usuario
SET A.login = U.login
WHERE A.login != U.login;

SELECT *
FROM
  produtoEstoqueAcerto;

SELECT A.*, M.xano
FROM
  produtoEstoqueAcerto         AS A
    INNER JOIN sqldados.stkmov AS M
               ON M.date = 20250315 AND
                  (M.remarks LIKE CONCAT('%E', A.numero) OR M.remarks LIKE CONCAT('%S', A.numero)) AND
                  M.storeno = A.numloja AND M.prdno = A.prdno AND M.grade = A.grade;

UPDATE produtoEstoqueAcerto AS A INNER JOIN sqldados.stkmov AS M ON M.date = 20250315 AND
                                                                    (M.remarks LIKE CONCAT('%E', A.numero) OR
                                                                     M.remarks LIKE CONCAT('%S', A.numero)) AND
                                                                    M.storeno = A.numloja AND M.prdno = A.prdno AND
                                                                    M.grade = A.grade
SET A.transacao = M.xano
WHERE A.transacao != M.xano;



ALTER TABLE produtoEstoqueAcerto
  ADD gravadoLogin int NULL DEFAULT 0 AFTER diferenca;

ALTER TABLE produtoEstoqueAcerto
  ADD gravado BOOLEAN NULL DEFAULT FALSE AFTER diferenca;


SELECT *
FROM
  produtoEstoqueAcerto
ORDER BY numloja, numero, prdno, grade;

SELECT *
FROM
  produtoEstoqueAcerto
WHERE numloja = 5
  AND numero IN (1, 2, 3)
ORDER BY numloja, numero, prdno, grade;


SELECT *
FROM
  produtoEstoqueAcerto
WHERE numloja = 5
  AND numero IN (3)
ORDER BY numloja, numero, prdno, grade;

SELECT *
FROM
  produtoEstoqueAcerto
WHERE numloja = 4
  AND numero IN (35, 36)
ORDER BY numloja, numero, prdno, grade;

/***********************************************/

SELECT numero,
       numloja,
       lojaSigla,
       data,
       hora,
       usuario,
       prdno,
       descricao,
       grade,
       diferenca,
       processado,
       transacao,
       login,
       estoqueCD,
       estoqueLoja,
       estoqueSis,
       gravadoLogin,
       gravado
FROM
  sqldados.produtoEstoqueAcerto;

ALTER TABLE produtoEstoqueAcerto
  DROP COLUMN lojaSigla;

ALTER TABLE produtoEstoqueAcerto
  DROP COLUMN descricao;


ALTER TABLE produtoEstoqueAcerto
  DROP COLUMN diferenca;

ALTER TABLE sqldados.produtoEstoqueAcerto
  ADD COLUMN acertoSimples boolean DEFAULT FALSE AFTER usuario;
