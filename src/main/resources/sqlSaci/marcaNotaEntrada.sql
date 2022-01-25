UPDATE sqldados.inv            AS I
  INNER JOIN sqldados.invnfe AS N
	       USING (invno)
SET I.s27 = 1
WHERE N.nfekey = :chave