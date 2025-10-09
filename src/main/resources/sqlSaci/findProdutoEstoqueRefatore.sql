set sql_mode='';

SELECT storeno,
       prdno,
       grade,
       observacao,
       SUBSTRING_INDEX(A.observacao, ',', 1) * 1 AS qtConferenciaObs,
       IF(A.observacao LIKE '%,%',
          SUBSTRING_INDEX(SUBSTRING_INDEX(A.observacao, ',', 2), ',', -1) * 1,
          0)                                     AS qtConfEditObs,
       IF(A.observacao LIKE '%,%,%',
          SUBSTRING_INDEX(SUBSTRING_INDEX(A.observacao, ',', 3), ',', -1) * 1,
          0)                                     AS qtConfEditLojaObs,
       qtConferencia,
       qtConfEdit,
       qtConfEditLoja
FROM
  sqldados.prdAdicional AS A
WHERE observacao IS NOT NULL
  AND observacao <> '';

ALTER TABLE sqldados.prdAdicional
  ADD COLUMN qtConferencia  int DEFAULT 0,
  ADD COLUMN qtConfEdit     int DEFAULT 0,
  ADD COLUMN qtConfEditLoja int DEFAULT 0;

UPDATE sqldados.prdAdicional A
SET A.qtConferencia  = SUBSTRING_INDEX(A.observacao, ',', 1) * 1,
    A.qtConfEdit     = IF(A.observacao LIKE '%,%',
                          SUBSTRING_INDEX(SUBSTRING_INDEX(A.observacao, ',', 2), ',', -1) * 1,
                          0),
    A.qtConfEditLoja = IF(A.observacao LIKE '%,%,%',
                          SUBSTRING_INDEX(SUBSTRING_INDEX(A.observacao, ',', 3), ',', -1) * 1,
                          0)
WHERE observacao IS NOT NULL
  AND observacao <> '';

select * from sqldados.prdAdicional where prdno = 19;
