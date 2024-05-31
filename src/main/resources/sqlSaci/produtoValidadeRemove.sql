UPDATE sqldados.produtoValidadeLoja
SET vencimentoDS = 0,
    estoqueDS    = 0
WHERE seq = :seq
  AND :storeno = 2;

UPDATE sqldados.produtoValidadeLoja
SET vencimentoMR = 0,
    estoqueMR    = 0
WHERE seq = :seq
  AND :storeno = 3;

UPDATE sqldados.produtoValidadeLoja
SET vencimentoMF = 0,
    estoqueMF    = 0
WHERE seq = :seq
  AND :storeno = 4;

UPDATE sqldados.produtoValidadeLoja
SET vencimentoPK = 0,
    estoquePK    = 0
WHERE seq = :seq
  AND :storeno = 5;

UPDATE sqldados.produtoValidadeLoja
SET vencimentoTM = 0,
    estoqueTM    = 0
WHERE seq = :seq
  AND :storeno = 8;

UPDATE sqldados.produtoValidadeLoja
SET vencimentoDS = 0,
    estoqueDS    = 0,
    vencimentoMR = 0,
    estoqueMR    = 0,
    vencimentoMF = 0,
    estoqueMF    = 0,
    vencimentoPK = 0,
    estoquePK    = 0,
    vencimentoTM = 0,
    estoqueTM    = 0
WHERE seq = :seq
  AND :storeno = 0

