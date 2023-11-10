UPDATE sqldados.eord
SET s10 = :userSing
WHERE storeno = :storeno
  AND ordno = :ordno

/*
select s10 from sqldados.eord
where storeno = 2
  and ordno = 3240797

select * from sqldados.users
where no = 1
*/