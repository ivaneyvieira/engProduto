SELECT *
FROM sqldados.prdAdicional
WHERE localizacao IS NULL;

UPDATE sqldados.prdAdicional
SET localizacao = ''
WHERE localizacao IS NULL;

ALTER TABLE sqldados.prdAdicional
  DROP PRIMARY KEY;

ALTER TABLE sqldados.prdAdicional
  ADD PRIMARY KEY (storeno, prdno, grade, localizacao);
