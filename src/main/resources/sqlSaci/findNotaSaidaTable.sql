USE sqldados;

DROP TABLE IF EXISTS nfSaidaAdiciona;
CREATE TABLE nfSaidaAdiciona
(
  storeno    SMALLINT DEFAULT 0  NOT NULL,
  pdvno      SMALLINT DEFAULT 0  NOT NULL,
  xano       INT      DEFAULT 0  NOT NULL,
  prdno      CHAR(16) DEFAULT '' NOT NULL,
  grade      CHAR(8)  DEFAULT '' NOT NULL,
  usuarioExp VARCHAR(100),
  usuarioCD  VARCHAR(100),
  marca      INT,
  PRIMARY KEY (storeno, pdvno, xano, prdno, grade)
);

INSERT INTO nfSaidaAdiciona(storeno, pdvno, xano, prdno, grade, usuarioExp, usuarioCD, marca)
SELECT storeno,
       pdvno,
       xano,
       prdno,
       grade,
       c5  AS usuarioExp,
       c4  AS usuarioCD,
       s11 AS marca
FROM sqldados.xaprd2
WHERE c5 != ''
   OR c4 != ''
   OR s11 != 0;