USE sqldados;

INSERT IGNORE sqldados.nfAutorizacao(storeno, pdvno, xano, usernoSing, tipoDev, observacao, impresso, dataInsert)
SELECT :storeno AS storeno,
       :pdvno   AS pdvno,
       :xano    AS xano,
       NULL     AS usernoSing,
       NULL     AS tipoDev,
       NULL     AS observacao,
       NULL     AS impresso,
       NULL     AS dataInsert
FROM
  dual;

UPDATE sqldados.nfAutorizacao
SET autoriza         = :autoriza,
    solicitacaoTroca = :solicitacaoTroca,
    produtoTroca     = :produtoTroca,
    userTroca        = :userTroca,
    userSolicitacao  = :userSolicitacao,
    motivoTroca      =:motivoTroca
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano