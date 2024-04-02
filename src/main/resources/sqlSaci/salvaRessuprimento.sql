SET SQL_MODE = '';
REPLACE sqldados.ordsAdicional(storeno, ordno, localizacao, observacao)
  VALUE (:storeno, :ordno, MID(:localizacao, 1, 4), :observacao)


