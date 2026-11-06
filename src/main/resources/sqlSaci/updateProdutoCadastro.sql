UPDATE sqldados.prd
SET mfno = :forn
WHERE no = :prdno
  AND :forn != 0;

UPDATE sqldados.prd
SET typeno = :tipo
WHERE no = :prdno
  AND :tipo != 0;


UPDATE sqldados.prd
SET clno   = :clno,
    deptno = MID(LPAD(:clno, 6, '0'), 1, 4) * 100,
    groupno= MID(LPAD(:clno, 6, '0'), 1, 2) * 10000
WHERE no = :prdno
  AND :clno != 0;

UPDATE sqldados.prd
SET taxno = :tributacao
WHERE no = :prdno
  AND :tributacao != '';


UPDATE sqldados.prdalq
SET form_label = :rotulo
WHERE prdno = :prdno
  AND :rotulo != '';

UPDATE sqldados.spedprd
SET ncm = :ncm
WHERE prdno = :prdno
  AND :ncm != ''

