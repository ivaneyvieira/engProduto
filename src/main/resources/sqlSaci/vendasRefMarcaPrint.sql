USE sqldados;

REPLACE sqldados.vendasRef(storeno, pdvno, xano, impresso, dataImpressao, horaImpessao) VALUE (:storeno, :pdvno, :xano,
                                                                                               :impresso,
                                                                                               IF(:impresso, CURDATE() * 1, NULL),
                                                                                               IF(:impresso, TIME_TO_SEC(CURRENT_TIME), NULL))
