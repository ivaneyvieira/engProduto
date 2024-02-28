USE sqldados;

CREATE TEMPORARY TABLE T_RESSU AS
SELECT COUNT(*) as value
FROM sqldados.ords
WHERE storeno = :storeno
  AND no = :ordno;


DELETE
FROM sqldados.ordsRessu
WHERE storeno = :storeno
  AND no = :ordno
  AND NOT EXISTS(SELECT *
                 FROM sqldados.ords
                 WHERE storeno = :storeno
                   AND no = :ordno);

DELETE
FROM sqldados.oprdRessu
WHERE storeno = :storeno
  AND ordno = :ordno
  AND NOT EXISTS(SELECT *
                 FROM sqldados.ords
                 WHERE storeno = :storeno
                   AND no = :ordno);


select value from T_RESSU