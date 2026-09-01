UPDATE sqldados.nf
SET remarks = MID(:obsNF, 1, 40)
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano