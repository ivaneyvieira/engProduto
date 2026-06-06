REPLACE sqldados.devClienteAutorizacao(invno, prdno, grade, userEntrega, userRecebimento, dataEntrega, horaEntrega,
                                       dataRecebimento, horaRecebimento)
VALUES (:invno, :prdno, :grade, :userEntrega, :userRecebimento, :dataEntrega, SEC_TO_TIME(:horaEntrega),
        :dataRecebimento, SEC_TO_TIME(:horaRecebimento))