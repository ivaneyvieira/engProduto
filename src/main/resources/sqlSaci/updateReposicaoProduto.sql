use sqldados;

replace sqldados.eoprdAdicional(storeno, ordno, prdno, grade, marca, qtRecebido, selecionado, posicao)
VALUES (:loja, :numero, :prdno, :grade, :marca, :qtRecebido, :selecionado, :posicao)