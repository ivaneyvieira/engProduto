UPDATE sqldados.prp
SET fob          = ROUND(:pcfabrica * 10000),
    ipi          = ROUND(:ipi * 100),

    package      = ROUND(:embalagem * 100),
    costdel3     = ROUND(:retido * 100),
    dicm         = ROUND(:icmsp * 100),
    freight      = ROUND(:frete * 100),
    freight_icms = ROUND(:freteICMS * 100),

    icm          = ROUND(:icms * 100),
    pis          = ROUND(:fcp * 100),
    finsoc       = ROUND(:pis * 100),
    comm         = ROUND(:ir * 100),

    adv          = ROUND(:contrib * 100),
    adm          = ROUND(:cpmf * 100),
    refpdel2     = ROUND(:fixa * 100),
    refpdel3     = ROUND(:outras * 100)
WHERE prdno = :prdno
  AND storeno = 10;

UPDATE sqldados.prd AS P
SET lucroTributado = ROUND(:mvap * 100),
    auxShort1      = ROUND(:creditoICMS * 100)
WHERE P.no = :prdno
