USE sqldados;

INSERT sqldados.vendasRef(storeno, pdvno, xano, impresso, dataImpressao, horaImpessao) VALUE (:storeno, :pdvno, :xano,
                                                                                              :impresso,
                                                                                              IF(:impresso, CURDATE() * 1, NULL),
                                                                                              IF(:impresso, TIME_TO_SEC(CURRENT_TIME), NULL))
ON DUPLICATE KEY UPDATE impresso = :impresso,
                        dataImpressao= IF(:impresso, CURDATE() * 1, NULL),
                        horaImpessao= IF(:impresso, TIME_TO_SEC(CURRENT_TIME), NULL)
