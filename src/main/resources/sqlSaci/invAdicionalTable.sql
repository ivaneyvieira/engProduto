CREATE TABLE sqldados.invAdicional
(
  invno  int PRIMARY KEY,
  volume int,
  peso   decimal(10, 4)
);

ALTER TABLE sqldados.invAdicional
  ADD carrno int NULL;

ALTER TABLE sqldados.invAdicional
  ADD situacaoDev int NULL;

ALTER TABLE sqldados.invAdicional
  ADD cet varchar(20) NULL;

ALTER TABLE sqldados.invAdicional
  ADD userno int NULL;

ALTER TABLE sqldados.invAdicional
  ADD dataDevolucao int NULL DEFAULT 0;

ALTER TABLE sqldados.invAdicional
  ADD observacao varchar(160) NULL DEFAULT '';

ALTER TABLE sqldados.invAdicional
  ADD dataColeta int NULL DEFAULT 0;

ALTER TABLE sqldados.invAdicional
  ADD empTermo int NULL DEFAULT 0;

ALTER TABLE sqldados.invAdicional
  ADD protocolo int NULL DEFAULT 0;


SELECT *
FROM
  sqldados.invAdicional;

USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_INVADICIONAL;
CREATE TEMPORARY TABLE T_INVADICIONAL
(
  PRIMARY KEY (invno, tipoDevolucao)
)
SELECT DISTINCT invno, tipoDevolucao
FROM
  sqldados.iprdAdicional
WHERE tipoDevolucao > 0;

ALTER TABLE sqldados.invAdicional
  ADD COLUMN tipoDevolucao int NOT NULL DEFAULT 0;

ALTER TABLE sqldados.invAdicional
  DROP PRIMARY KEY;

ALTER TABLE sqldados.invAdicional
  ADD PRIMARY KEY (invno, tipoDevolucao);

INSERT IGNORE INTO sqldados.invAdicional(invno, volume, peso, carrno, situacaoDev, cet, userno, tipoDevolucao)
SELECT invno, volume, peso, carrno, situacaoDev, cet, userno, T.tipoDevolucao
FROM
  sqldados.invAdicional       AS I
    INNER JOIN T_INVADICIONAL AS T
               USING (invno);

SELECT *
FROM
  T_INVADICIONAL;

/************************************************************************************/

SELECT *
FROM
  sqldados.invAdicional;

ALTER TABLE sqldados.invAdicional
  ADD COLUMN numero int NOT NULL DEFAULT 0 AFTER invno;

ALTER TABLE sqldados.invAdicional
  DROP PRIMARY KEY;

ALTER TABLE sqldados.invAdicional
  ADD PRIMARY KEY (invno, tipoDevolucao, numero);

DROP TABLE IF EXISTS T_IPRADICIONALDEV;
CREATE TEMPORARY TABLE T_IPRADICIONALDEV
(
  PRIMARY KEY (invno, tipoDevolucao, numero)
)
SELECT invno, tipoDevolucao, numero
FROM
  iprdAdicionalDev
GROUP BY invno, tipoDevolucao, numero;

UPDATE sqldados.invAdicional AS A
  INNER JOIN T_IPRADICIONALDEV AS T
  USING (invno, tipoDevolucao)
SET A.numero = T.numero
WHERE A.numero = 0;

DROP TABLE IF EXISTS sqldados.appNumeracao;
CREATE TABLE sqldados.appNumeracao
(
    nome   varchar(50) PRIMARY KEY,
    numero int NOT NULL
);

