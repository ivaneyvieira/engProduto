DROP TEMPORARY TABLE IF EXISTS T_AUTO;
CREATE TEMPORARY TABLE T_AUTO
SELECT *
FROM sqldados.nfAutorizacao
WHERE storeno = :loja
  AND pdvno = :pdv
  AND xano = :transacao;

REPLACE sqldados.nfAutorizacao(storeno, pdvno, xano, invno, usernoSing, tipoDev, observacao, impresso, dataInsert,
                               autoriza, solicitacaoTroca, produtoTroca, userTroca, userSolicitacao, motivoTroca,
                               motivoTrocaCod, nfEntRet)
SELECT storeno,
       pdvno,
       xano,
       :ni AS invno,
       usernoSing,
       tipoDev,
       observacao,
       impresso,
       dataInsert,
       autoriza,
       solicitacaoTroca,
       produtoTroca,
       userTroca,
       userSolicitacao,
       motivoTroca,
       motivoTrocaCod,
       nfEntRet
FROM T_AUTO