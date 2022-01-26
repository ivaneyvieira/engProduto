DO @INV := (SELECT MAX(invno + 1)
	    FROM sqldados.invConferencia);

INSERT IGNORE sqldados.invConferencia(invno, storeno, nfname, invse, issue_date, nfekey)
VALUES (IFNULL(@INV, 1), 0, '', '', current_date * 1, :chave)