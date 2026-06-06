REPLACE sqldados.devClienteAutorizacao(invno, prdno, grade, userEntrega, userRecebimento, dataEntrega, horaEntrega,
                                       dataRecebimento, horaRecebimento)
VALUES (:invno, :prdno, :grade, :userEntrega, :userRecebimento, IF(:dataEntrega = 0, NULL, :dataEntrega),
        IF(:horaEntrega = 0, NULL, SEC_TO_TIME(:horaEntrega)), IF(:dataRecebimento = 0, NULL, :dataRecebimento),
        IF(:horaRecebimento = 0, NULL, SEC_TO_TIME(:horaRecebimento)))