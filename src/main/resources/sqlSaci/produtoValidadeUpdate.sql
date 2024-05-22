REPLACE INTO sqldados.produtoValidadeLoja (prdno, grade, vencimentoDS, estoqueDS, vencimentoMR, estoqueMR, vencimentoMF,
                                           estoqueMF, vencimentoPK, estoquePK, vencimentoTM, estoqueTM)
SELECT :prdno,
       :grade,
       :vencimentoDS,
       :estoqueDS,
       :vencimentoMR,
       :estoqueMR,
       :vencimentoMF,
       :estoqueMF,
       :vencimentoPK,
       :estoquePK,
       :vencimentoTM,
       :estoqueTM
FROM dual
WHERE :seq = 0;

REPLACE INTO sqldados.produtoValidadeLoja (seq, prdno, grade, vencimentoDS, estoqueDS, vencimentoMR, estoqueMR,
                                           vencimentoMF, estoqueMF, vencimentoPK, estoquePK, vencimentoTM, estoqueTM)
SELECT :seq,
       :prdno,
       :grade,
       :vencimentoDS,
       :estoqueDS,
       :vencimentoMR,
       :estoqueMR,
       :vencimentoMF,
       :estoqueMF,
       :vencimentoPK,
       :estoquePK,
       :vencimentoTM,
       :estoqueTM
FROM dual
WHERE :seq != 0
