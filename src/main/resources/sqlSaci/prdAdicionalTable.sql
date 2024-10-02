select * from sqldados.prdAdicional
where localizacao is null;

UPDATE sqldados.prdAdicional
SET localizacao = ''
WHERE localizacao IS NULL;

alter table sqldados.prdAdicional
  drop primary key;

alter table sqldados.prdAdicional
  add primary key(storeno, prdno, grade, localizacao);
