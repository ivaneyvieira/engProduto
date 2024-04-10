SET SQL_MODE = '';
REPLACE sqldados.ordsAdicional(storeno, ordno, localizacao, observacao)
  VALUE (:storeno, :ordno, :localizacoes, :observacao)

/*
alter table sqldados.ordsAdicional
  modify column localizacao varchar(100) not null

select * from sqldados.ordsAdicional
*/
