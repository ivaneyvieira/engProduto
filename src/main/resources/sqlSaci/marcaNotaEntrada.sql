INSERT IGNORE sqldados.invConferencia(issue_date, nfekey)
VALUES (CURRENT_DATE * 1, :chave)