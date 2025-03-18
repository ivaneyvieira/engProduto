SELECT *
FROM
  sqldados.prdAdicional
WHERE localizacao IS NULL;

UPDATE sqldados.prdAdicional
SET localizacao = ''
WHERE localizacao IS NULL;

ALTER TABLE sqldados.prdAdicional
  DROP PRIMARY KEY;

ALTER TABLE sqldados.prdAdicional
  ADD PRIMARY KEY (storeno, prdno, grade);


ALTER TABLE sqldados.prdAdicional
  ADD COLUMN estoqueCD INT NULL;

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN estoqueLoja INT NULL;

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN estoqueData DATE NULL;

UPDATE sqldados.prdAdicional
SET estoqueData = dataObservacao
WHERE dataObservacao != estoqueData
   OR dataObservacao IS NOT NULL;

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN estoqueUser int NULL;


SELECT *
FROM
  sqldados.prdAdicional;

UPDATE sqldados.prdAdicional
SET estoqueCD = observacao * 1
WHERE observacao REGEXP '^[0-9]+$';

SELECT *
FROM
  sqldados.prdAdicional
WHERE observacao != '';