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
  ADD estoqueSis   int NULL AFTER grade;


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
