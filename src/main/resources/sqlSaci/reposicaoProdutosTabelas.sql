USE sqldados;

SET SQL_MODE = '';

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
