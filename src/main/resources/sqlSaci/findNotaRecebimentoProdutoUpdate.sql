USE sqldados;

SET SQL_MODE = '';

UPDATE sqldados.iprdAdicional
SET marcaRecebimento = :marca
WHERE invno = :ni
  AND prdno = :prdno
  AND grade = :grade