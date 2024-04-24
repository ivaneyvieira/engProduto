SET SQL_MODE = '';

DELETE FROM sqldados.ordsAdicional
WHERE storeno = :storeno
  AND ordno = :ordno;

REPLACE sqldados.ordsAdicional(storeno, ordno, localizacao, observacao)
  VALUE (:storeno, :ordno, :localizacoes, :observacao)

/*
alter table sqldados.ordsAdicional
  modify column localizacao varchar(100) not null

select * from sqldados.ordsAdicional
*/
