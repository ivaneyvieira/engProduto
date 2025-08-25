create table sqldados.invAdicional_447_bak
select *
from sqldados.invAdicional
where numero = 447;


select *
from sqldados.invAdicional
where numero = 467
order by situacaoDev;

select D.invno, D.tipoDevolucao, D.numero, A.invno, A.tipoDevolucao, A.numero
from sqldados.iprdAdicionalDev AS D
         left join sqldados.invAdicional AS A
                   USING (invno, tipoDevolucao, numero)
where numero = 467;

select *
from sqldados.iprdAdicionalDev AS D
where numero = 467;



select *
from sqldados.iprdAdicionalDev
where (invno, tipoDevolucao, numero) in (select distinct invno, tipoDevolucao, numero
                                         from sqldados.invAdicional
                                         where numero = 467);


select invno, COUNT(situacaoDev)
from sqldados.invAdicional
where numero = 447
GROUP BY invno;


update sqldados.invAdicional
set situacaoDev;

select *
from sqldados.oprd
where ordno = 500018597
  and storeno = 1;

select storeno, ordno, prdno, grade, seqno, auxShort4
from sqldados.oprdRessu
where ordno = 500018597
  and storeno = 1;

select *
from sqldados.ords
where no = 500018597
  and storeno = 1;

select *
from sqldados.ordsRessu
where no = 500018597
  and storeno = 1;

/*************************************************************************************/

insert into sqldados.ords(no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno, dataFaturamento,
                          invno, freightAmt, auxLong1, auxLong2, amtOrigem, dataEntrega, discountOrig, l1, l2, l3, l4,
                          m1, m2, m3, m4, deliv, storeno, carrno, empno, prazo, eord_storeno, delivOriginal, bits,
                          bits2, bits3, padbyte, indxno, repno, auxShort1, auxShort2, noofinst, status, s1, s2, s3, s4,
                          frete, remarks, ordnoFromVend, remarksInv, remarksRcv, remarksOrd, auxChar, c1, c2, c3, c4)
select no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno, dataFaturamento,
       invno, freightAmt, auxLong1, auxLong2, amtOrigem, dataEntrega, discountOrig, l1, l2, l3, l4,
       m1, m2, m3, m4, deliv, storeno, carrno, empno, prazo, eord_storeno, delivOriginal, bits,
       bits2, bits3, padbyte, indxno, repno, auxShort1, auxShort2, noofinst, status, s1, s2, s3, s4,
       frete, remarks, ordnoFromVend, remarksInv, remarksRcv, remarksOrd, auxChar, c1, c2, c3, c4
from sqldados.ordsRessu
where no = 500018597
  and storeno = 1;

insert into sqldados.oprd(ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2, icmsSubst, auxLong3,
                          auxLong4, auxMy3, auxMy4, qtty, qtty_src, qtty_xfr, cost, qttyRcv, qttyCancel, qttyVendaMes,
                          qttyVendaMesAnt, qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, storeno, seqno, status,
                          bits, bits2, auxShort1, auxShort2, auxShort3, auxShort4, prdno, grade, remarks, padbyte,
                          gradeFechada, obs, auxStr)
select ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2, icmsSubst, auxLong3,
       auxLong4, auxMy3, auxMy4, qtty, qtty_src, qtty_xfr, cost, qttyRcv, qttyCancel, qttyVendaMes,
       qttyVendaMesAnt, qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, storeno, seqno, status,
       bits, bits2, auxShort1, auxShort2, auxShort3, auxShort4, prdno, grade, remarks, padbyte,
       gradeFechada, obs, auxStr
from sqldados.oprdRessu
where ordno = 500018597
  and storeno = 1;