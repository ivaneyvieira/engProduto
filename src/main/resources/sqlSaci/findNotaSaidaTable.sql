USE sqldados;

DROP TABLE IF EXISTS nfSaidaAdiciona;
CREATE TABLE nfSaidaAdiciona
(
  storeno          SMALLINT DEFAULT 0  NOT NULL,
  pdvno            SMALLINT DEFAULT 0  NOT NULL,
  xano             INT      DEFAULT 0  NOT NULL,
  prdno            CHAR(16) DEFAULT '' NOT NULL,
  grade            CHAR(8)  DEFAULT '' NOT NULL,
  usuarioExp       VARCHAR(100),
  usuarioCD        VARCHAR(100),
  marca            INT,
  gradeAlternativa CHAR(8)  DEFAULT '' NOT NULL,
  PRIMARY KEY (storeno, pdvno, xano, prdno, grade)
);

INSERT INTO nfSaidaAdiciona(storeno, pdvno, xano, prdno, grade, usuarioExp, usuarioCD, marca, gradeAlternativa)
SELECT storeno, pdvno, xano, prdno, grade, c5 AS usuarioExp, c4 AS usuarioCD, s11 AS marca, c6 AS gradeAlternativa
FROM
  sqldados.xaprd2
WHERE c5 != ''
   OR c4 != ''
   OR s11 != 0;

SELECT *
FROM
  nfSaidaAdiciona;

SELECT *
FROM
  sqlpdv.pxaprd
ORDER BY xano DESC;


/*******************************************/

CREATE TABLE sqldados.xaprd2Marca
(
  storeno  SMALLINT DEFAULT 0  NOT NULL,
  pdvno    SMALLINT DEFAULT 0  NOT NULL,
  xano     INT      DEFAULT 0  NOT NULL,
  prdno    CHAR(16) DEFAULT '' NOT NULL,
  grade    CHAR(8)  DEFAULT '' NOT NULL,
  marca    INT,
  impresso INT,
  PRIMARY KEY (storeno, pdvno, xano, prdno, grade)
);

REPLACE sqldados.xaprd2Marca(storeno, pdvno, xano, prdno, grade, marca, impresso)
SELECT storeno, pdvno, xano, prdno, grade, s11 AS marca, s11 AS impresso
FROM
  sqldados.xaprd2
WHERE s11 > 0
   OR s11 > 0;




