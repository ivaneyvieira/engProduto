USE sqldados;

DROP TABLE IF EXISTS produtoEstoqueAcertoMobile;
CREATE TABLE produtoEstoqueAcertoMobile
(
  numero       int                    NOT NULL,
  numloja      int                    NOT NULL,
  lojaSigla    varchar(2)             NOT NULL,
  data         date                   NOT NULL,
  hora         time                   NOT NULL,
  usuario      varchar(50)            NOT NULL,
  prdno        varchar(16)            NOT NULL,
  descricao    varchar(100)           NOT NULL,
  grade        varchar(20)            NOT NULL,
  estoqueSis   int                    NULL,
  estoqueLoja  int                    NULL,
  estoqueCD    int                    NULL,
  diferenca    int                    NOT NULL,
  gravado      tinyint(1)  DEFAULT 0  NULL,
  gravadoLogin int         DEFAULT 0  NULL,
  processado   tinyint(1)  DEFAULT 0  NULL,
  transacao    varchar(20) DEFAULT '' NULL,
  login        varchar(20) DEFAULT '' NULL,
  PRIMARY KEY (numloja, numero, prdno, grade)
)
  ENGINE = MyISAM
  CHARSET = latin1;

INSERT INTO produtoEstoqueAcertoMobile (numero, numloja, lojaSigla, data, hora, login, usuario, prdno, descricao, grade,
                                        estoqueSis, estoqueCD, estoqueLoja, diferenca, gravadoLogin, gravado)
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
       gravadoLogin,
       gravado
FROM
  produtoEstoqueAcerto;