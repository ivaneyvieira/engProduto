use sqldados;

drop temporary table if exists T_PRD;
create temporary table T_PRD
(
    primary key (prdno)
)
select no as prdno, if(MID(grade_l, 1, 10) = 0, 'N', 'S') as temGrade, grade_l
from sqldados.prd;

drop temporary table if exists T_PRD_GRADE;
create temporary table T_PRD_GRADE
(
    primary key (prdno, grade)
)
select prdno, grade
from sqldados.prdloc AS L
         left join T_PRD AS P
                   using (prdno)
where storeno = 4
group by prdno, grade;


drop temporary table if exists T_PRD_LOC;
create temporary table T_PRD_LOC
(
    primary key (prdno, grade, storeno, localizacao)
)
select prdno, grade, S.no as storeno, 'CD' as localizacao
from T_PRD_GRADE AS G
         inner join sqldados.store AS S
                    ON S.no in (2, 3, 5, 8)
GROUP BY prdno, grade, storeno, localizacao
ORDER BY prdno, grade, storeno, localizacao;

REPLACE INTO prdAdicional(storeno, prdno, grade, localizacao)
select storeno, prdno, grade, localizacao
from T_PRD_LOC AS L
where storeno != 4;

REPLACE INTO sqldados.prdloc(stkmin, stkmax, storeno, bits, prdno, localizacao, grade)
SELECT 0 as stkmin, 0 as stkmax, storeno, 0 as bits, prdno, localizacao, grade
FROM T_PRD_LOC;

REPLACE INTO sqldados.prdloc2(stkmin, stkmax, l1, l2, l3, l4, l5, l6, l7, l8, m1, m2, m3, m4, m5, m6, m7, m8,
                              storeno, sano, bits, s1, s2, s3, s4, s5, s6, s7, prdno, grade, localizacao, c1, c2)
select 0  as stkmin,
       0  as stkmax,
       0  as l1,
       0  as l2,
       0  as l3,
       0  as l4,
       0  as l5,
       0  as l6,
       0  as l7,
       0  as l8,
       0  as m1,
       0  as m2,
       0  as m3,
       0  as m4,
       0  as m5,
       0  as m6,
       0  as m7,
       0  as m8,
       storeno,
       0  as sano,
       0  as bits,
       0  as s1,
       0  as s2,
       0  as s3,
       0  as s4,
       0  as s5,
       0  as s6,
       0  as s7,
       prdno,
       grade,
       localizacao,
       '' as c1,
       '' as c2
FROM T_PRD_LOC AS L
         LEFT JOIN sqldados.prdloc2 AS L2
                   USING (prdno, grade, storeno, localizacao)
WHERE L2.prdno IS NULL
GROUP BY L.prdno, L.grade, L.storeno, L.localizacao;



