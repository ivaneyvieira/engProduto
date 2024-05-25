REPLACE INTO sqldados.produtoValidadeLoja (prdno, grade, dataEntrada, vencimento,
                                           vencimentoDS, estoqueDS,
                                           vencimentoMR, estoqueMR,
                                           vencimentoMF, estoqueMF,
                                           vencimentoPK, estoquePK,
                                           vencimentoTM, estoqueTM)
SELECT :prdno,
       :grade,
       :dataEntrada,
       :vencimento,
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

REPLACE INTO sqldados.produtoValidadeLoja (seq, prdno, grade, dataEntrada, vencimento,
                                           vencimentoDS, estoqueDS,
                                           vencimentoMR, estoqueMR,
                                           vencimentoMF, estoqueMF,
                                           vencimentoPK, estoquePK,
                                           vencimentoTM, estoqueTM)
SELECT :seq,
       :prdno,
       :grade,
       :dataEntrada,
       :vencimento,
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
