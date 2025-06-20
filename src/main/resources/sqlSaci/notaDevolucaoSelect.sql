DROP TABLE IF EXISTS sqldados.invDevolucao;
CREATE TABLE sqldados.invDevolucao
(
  INDEX (storeno, nfo, motivo)
)
SELECT storeno                             AS storeno,
       CONCAT(nfno, '/', nfse)             AS notaDevolucao,
       CAST(issuedate AS date)             AS emissaoDevolucao,
       grossamt / 100                      AS valorDevolucao,
       print_remarks                       AS obsDevolucao,
       status = 1                          AS cancelado,
       IF(LOCATE(' NFO ', print_remarks) > 0,
          SUBSTRING_INDEX(
              SUBSTRING(print_remarks,
                        LOCATE(' NFO ', print_remarks) + 5,
                        100), ' ', 1), '') AS nfo,
       CASE
         WHEN print_remarks REGEXP 'AVARIA'            THEN 1
         WHEN print_remarks REGEXP 'FAL.{1,10}TRANSP'  THEN 2
         WHEN print_remarks REGEXP 'FAL.{1,10}FAB'     THEN 3
         WHEN print_remarks REGEXP 'VENCIM|VENCID'     THEN 4
         WHEN print_remarks REGEXP 'DEFEITO.{1,10}FAB' THEN 7
         WHEN print_remarks REGEXP 'SEM.{1,10}IDENTIF' THEN 5
         WHEN print_remarks REGEXP 'DESAC.{1,10}PED'   THEN 6
                                                       ELSE 0
       END                                 AS motivo
FROM
  sqldados.nf
WHERE storeno IN (1, 2, 3, 4, 5, 8)
  AND issuedate >= 20240101
  AND tipo = 2
  AND print_remarks REGEXP '[^0-9][0-9]+/[0-9]+[^0-9]';

