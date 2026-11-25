USE sqldados;

INSERT sqldados.vendasRef(storeno, pdvno, xano, userSeparacao) VALUE (:storeno, :pdvno, :xano, :userSeparacao)
ON DUPLICATE KEY UPDATE userSeparacao = :userSeparacao
