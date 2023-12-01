UPDATE sqldados.eord
SET status = 2,
    s11    = :user
WHERE storeno = :storeno
  AND ordno = :ordno
  AND status = 1;

UPDATE sqldados.eoprd
SET status = 2
WHERE storeno = :storeno
  AND ordno = :ordno
  AND status = 1;

UPDATE sqldados.eoprd2
SET status = 2
WHERE storeno = :storeno
  AND ordno = :ordno
  AND status = 1


