DO @INV := (SELECT MAX(invno + 1)
	    FROM sqldados.invConferencia);

INSERT IGNORE sqldados.invConferencia(invno, storeno, nfname, invse, issue_date, nfekey)
VALUES (IFNULL(@INV, 1), 0, '', '', CURRENT_DATE * 1, :chave)