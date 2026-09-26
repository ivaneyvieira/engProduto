USE sqldados;

REPLACE INTO sqldados.nfSolicitacaoCancelar(storeno, pdvno, xano, motivo, userCancel) VALUE (:storeno, :pdvno, :xano, :motivo, :userCancel);

DELETE
FROM sqldados.nfSolicitacaoCancelar
WHERE motivo = ''

