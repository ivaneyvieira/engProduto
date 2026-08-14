USE sqldados;

SET sql_mode = '';

REPLACE sqldados.dadosDev(invno, userSolicitacao, userTroca, produtoTroca, tipoDev, nfEntRet) VALUE (:invno,
                                                                                                     :userSolicitacao,
                                                                                                     :userTroca,
                                                                                                     :produtoTroca,
                                                                                                     :tipoDev,
                                                                                                     :nfEntRet)