USE sqldados;

SET sql_mode = '';

UPDATE sqldados.dadosDev
SET nfEntRet = :nfEntRet
WHERE invno = :invno