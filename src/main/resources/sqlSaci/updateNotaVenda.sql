USE sqldados;


INSERT IGNORE sqldados.nfAutorizacao(storeno, pdvno, xano, invno, usernoSing, tipoDev, observacao, impresso, dataInsert)
SELECT :storeno AS storeno,
       :pdvno   AS pdvno,
       :xano    AS xano,
       :invno   AS invno,
       NULL     AS usernoSing,
       NULL     AS tipoDev,
       NULL     AS observacao,
       NULL     AS impresso,
       NULL     AS dataInsert
FROM dual;

UPDATE sqldados.nfAutorizacao
SET autoriza         = :autoriza,
    solicitacaoTroca = :solicitacaoTroca,
    produtoTroca     = :produtoTroca,
    userTroca        = :userTroca,
    userSolicitacao  = :userSolicitacao,
    motivoTroca      = :motivoTroca,
    motivoTrocaCod   = :motivoTrocaCod,
    nfEntRet         = :nfEntRet
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano
  AND invno = :invno