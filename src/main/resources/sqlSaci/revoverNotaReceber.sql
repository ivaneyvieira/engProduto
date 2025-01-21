DELETE
FROM
  sqldados.iprdConferencia
WHERE nfekey = :nfekey;

DELETE
FROM
  sqldados.invConferencia
WHERE nfekey = :nfekey;