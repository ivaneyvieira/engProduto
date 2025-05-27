USE sqldados;

DROP TABLE IF EXISTS sqldados.iprdAdicionalDev;
CREATE TABLE sqldados.iprdAdicionalDev
(
  invno          int           NOT NULL,
  prdno          varchar(16)   NOT NULL,
  grade          varchar(8)    NOT NULL,
  numero         int           NOT NULL,
  tipoDevolucao  int DEFAULT 0 NOT NULL,
  quantDevolucao int DEFAULT 0 NULL,
  PRIMARY KEY (invno, prdno, grade, tipoDevolucao, numero)
);

SELECT @rowcount := 0;

DROP TEMPORARY TABLE IF EXISTS T_SEQ;
CREATE TEMPORARY TABLE T_SEQ
(
  PRIMARY KEY (invno)
)
SELECT @rowcount := @rowcount + 1 AS seq, invno
FROM
  sqldados.iprdAdicional
WHERE quantDevolucao > 0
GROUP BY invno
ORDER BY invno;

SELECT *
FROM
  T_SEQ;

INSERT INTO sqldados.iprdAdicionalDev(invno, prdno, grade, numero, tipoDevolucao, quantDevolucao)
SELECT invno, prdno, grade, seq AS numero, tipoDevolucao, quantDevolucao
FROM
  sqldados.iprdAdicional
    INNER JOIN T_SEQ
               USING (invno)
WHERE quantDevolucao > 0;

SELECT *
FROM
  sqldados.iprdAdicionalDev;

/******************************************/

DROP TABLE IF EXISTS invAdicionalDevArquivo;
CREATE TABLE invAdicionalDevArquivo
(
  invno         int           NOT NULL,
  numero        int           NOT NULL,
  tipoDevolucao int DEFAULT 0 NOT NULL,
  seq           int AUTO_INCREMENT PRIMARY KEY,
  date          int           NOT NULL,
  filename      varchar(100)  NOT NULL,
  file          mediumblob    NULL,
  INDEX (invno, tipoDevolucao, numero)
);

DROP TEMPORARY TABLE IF EXISTS T_FILE;
CREATE TEMPORARY TABLE T_FILE
SELECT seq,
       invno,
       CASE title
         WHEN 'AVARIA_TRANSPORTE' THEN 1
         WHEN 'FALTA_TRANSPORTE'  THEN 2
         WHEN 'FALTA_FABRICA'     THEN 3
         WHEN 'VENCIMENTO'        THEN 4
         WHEN 'DEFEITO_FABRICA'   THEN 7
         WHEN 'SEM_IDENTIFICACAO' THEN 5
         WHEN 'EM_DESACORDO'      THEN 6
         WHEN 'EM_GARANTIA'       THEN 8
                                  ELSE 0
       END AS tipoDevolucao,
       date,
       filename,
       file
FROM
  invAdicionalArquivos
HAVING tipoDevolucao > 0;


INSERT INTO invAdicionalDevArquivo(invno, tipoDevolucao, numero, date, filename, file)
SELECT invno, tipoDevolucao, numero, date, filename, file
FROM
  sqldados.iprdAdicionalDev
    INNER JOIN T_FILE
               USING (invno, tipoDevolucao)
WHERE quantDevolucao > 0;

SELECT invno, numero, tipoDevolucao, seq, date, filename, file
FROM
  invAdicionalDevArquivo;


/****************************************************************************************/

ALTER TABLE sqldados.iprdAdicionalDev
  ADD COLUMN observacao varchar(160) DEFAULT '';

ALTER TABLE sqldados.iprdAdicionalDev
  DROP COLUMN observacao;


SELECT *
FROM
  sqldados.invAdicional;

ALTER TABLE sqldados.iprdAdicionalDev
  ADD COLUMN seq int(10) NULL DEFAULT NULL;