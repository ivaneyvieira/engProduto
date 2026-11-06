UPDATE sqldados.prd
SET mfno   = :forn,
    typeno = :tipo,
    clno   = :clno,
    deptno = MID(LPAD(:clno, 6, '0'), 1, 4) * 100,
    groupno= MID(LPAD(:clno, 6, '0'), 1, 2) * 10000
WHERE no = :prdno;

UPDATE sqldados.prdalq
SET form_label = :rotulo
WHERE prdno = :prdno;

UPDATE sqldados.spedprd
SET ncm    = :ncm,
    cstPis = :cst
WHERE prdno = :prdno