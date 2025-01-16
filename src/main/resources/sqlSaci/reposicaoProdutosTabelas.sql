USE
sqldados;

SET
SQL_MODE = '';

DROP TABLE IF EXISTS sqldados.eordAdicional;
CREATE TABLE sqldados.eordAdicional
(
    storeno     INT,
    ordno       INT,
    localizacao VARCHAR(4),
    empEntregue INT,
    empRecebido INT,
    observacao  TEXT,
    PRIMARY KEY (ordno, storeno, localizacao)
);


DROP TABLE IF EXISTS sqldados.eoprdAdicional;
CREATE TABLE sqldados.eoprdAdicional
(
    storeno     INT,
    ordno       INT,
    prdno       VARCHAR(16),
    grade       VARCHAR(8),
    marca       INT,
    qtRecebido  INT,
    selecionado INT,
    posicao     INT,
    PRIMARY KEY (ordno, storeno, prdno, grade)
);

ALTER TABLE sqldados.eoprdAdicional
    ADD COLUMN empEntregue INT NULL;

ALTER TABLE sqldados.eoprdAdicional
    ADD COLUMN empRecebido INT NULL;

DROP
TEMPORARY TABLE IF EXISTS T_LOC;
CREATE
TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno AS prdno,
       A.grade AS grade,
       MID(COALESCE(A.localizacao, ''), 1, 4) AS localizacao
FROM sqldados.prdAdicional AS A
WHERE A.storeno = 4
  AND A.localizacao != ''
GROUP BY A.storeno, A.prdno, A.grade;

DROP
TEMPORARY TABLE IF EXISTS T_LOC_PRD;
CREATE
TEMPORARY TABLE T_LOC_PRD
(
  PRIMARY KEY (ordno, storeno, prdno, grade)
)
SELECT ordno, storeno, E.prdno, E.grade, OA.empEntregue, OA.empRecebido
FROM sqldados.eordAdicional AS OA
         INNER JOIN sqldados.eoprd AS E
                    USING (ordno, storeno)
         INNER JOIN T_LOC AS L
                    ON E.prdno = L.prdno
                        AND E.grade = L.grade
                        AND OA.localizacao = L.localizacao
WHERE storeno = 4;

REPLACE
INTO sqldados.eoprdAdicional (storeno, ordno, prdno, grade, marca, qtRecebido, selecionado, posicao,
                                      empEntregue, empRecebido)
SELECT storeno,
       ordno,
       prdno,
       grade,
       marca,
       qtRecebido,
       selecionado,
       posicao,
       P.empEntregue,
       P.empRecebido
FROM T_LOC_PRD AS P
         INNER JOIN sqldados.eoprdAdicional AS EA
                    USING (storeno, ordno, prdno, grade);


SELECT *
FROM sqldados.eordAdicional;

SELECT ordno
FROM sqldados.eord
WHERE date = 20240909;



SELECT *
FROM sqldados.eoprdAdicional
WHERE marca IS NOT NULL
  AND ordno IN (SELECT ordno
                FROM sqldados.eord
                WHERE
    date = 20240909);

UPDATE sqldados.eoprdAdicional
SET marca       = 1,
    selecionado = 1,
    posicao     = 0
WHERE marca IS NULL
  AND ordno IN (SELECT ordno
                FROM sqldados.eord
                WHERE
    date >= 20240901);


/**************************************************************/

ALTER TABLE sqldados.eoprdAdicional
    ADD COLUMN empFinalizado INT NULL;

UPDATE sqldados.eoprdAdicional
SET empFinalizado = empEntregue
WHERE empFinalizado = 0
   OR empFinalizado IS NULL;

