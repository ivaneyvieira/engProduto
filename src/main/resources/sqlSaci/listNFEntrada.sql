SET SQL_MODE = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
SELECT N.id                                                                    AS id,
       L.no                                                                    AS loja,
       L.sname                                                                 AS sigla,
       NUMERO                                                                  AS numero,
       SERIE                                                                   AS serie,
       dataEmissao                                                             AS dataEmissao,
       NULL                                                                    AS dataEntrada,
       V.no                                                                    AS fornecedorNota,
       C.no                                                                    AS fornecedorCad,
       cnpjEmitente                                                            AS cnpjEmitente,
       nomeFornecedor                                                          AS nomeFornecedor,
       valorTotalProdutos                                                      AS valorTotalProdutos,
       MID(N.xmlNfe,
           LOCATE('<vNF>', N.xmlNfe) + 5,
           LOCATE('</vNF>', N.xmlNfe)
             - LOCATE('<vNF>', N.xmlNfe) - 5) * 1                              AS valorTotal,
       @P1 := REGEXP_SUBSTR(MID(N.xmlNfe,
                                LOCATE('<xPed>', N.xmlNfe) + 6,
                                LOCATE('</xPed>', N.xmlNfe)
                                  - LOCATE('<xPed>', N.xmlNfe) - 6), '[0-9]+') AS pedidoInd,
       @P2 := REGEXP_SUBSTR(REGEXP_SUBSTR(N.xmlNfe, CONCAT(L.sname,
                                                           '[^0-9]{0,4}[0-9]{4,20}')),
                            '[0-9]+')                                          AS pedidoReg,
       IF(IFNULL(@P1, 0) > IFNULL(@P2, 0), IFNULL(@P1, 0), IFNULL(@P2, 0)) * 1 AS pedido,
       N.chave                                                                 AS chave,
       xmlNfe                                                                  AS xmlNfe,
       IF(I2.invno IS NULL, 'N', 'S')                                          AS preEntrada
FROM sqldados.notasEntradaNdd AS N
       INNER JOIN sqldados.store AS L
                  ON N.cnpjDestinatario = L.cgc
       LEFT JOIN sqldados.inv AS I
                 ON N.numero = I.nfname
                   AND N.serie = I.invse
                   AND L.no = I.storeno
       LEFT JOIN sqldados.inv2 AS I2
                 ON N.numero = I2.nfname
                   AND N.serie = I2.invse
                   AND L.no = I2.storeno
       LEFT JOIN sqldados.vend AS V
                 ON V.cgc = N.cnpjEmitente
       LEFT JOIN sqldados.custp AS C
                 ON C.cpf_cgc = N.cnpjEmitente
WHERE dataEmissao >= 20241101
  AND (N.cnpjEmitente NOT LIKE '07.483.654%')
  AND (I.invno IS NULL)
  AND (N.dataEmissao >= :dataInicial OR :dataInicial = 0)
  AND (N.dataEmissao <= :dataFinal OR :dataFinal = 0)
  AND (N.NUMERO = :numero OR :numero = 0)
  AND (V.cgc = :cnpj OR :cnpj = '')
  AND (L.no = :loja OR :loja = 0)
  AND (V.name LIKE CONCAT(:fornecedor, '%') OR
       (V.no = :fornecedor) OR :fornecedor = '')
  AND ((:preEntrada = 'S' AND I2.invno IS NOT NULL) OR
       (:preEntrada = 'N' AND I2.invno IS NULL) OR
       (:preEntrada = 'T'));


SELECT id,
       loja,
       sigla,
       numero,
       serie,
       dataEmissao,
       dataEntrada,
       fornecedorNota,
       fornecedorCad,
       cnpjEmitente,
       nomeFornecedor,
       valorTotalProdutos,
       valorTotal,
       pedido,
       CAST(O.date AS DATE) AS dataPedido,
       chave,
       xmlNfe,
       preEntrada
FROM T_NOTA AS N
       LEFT JOIN sqldados.ords AS O
                 ON O.storeno = N.loja
                   AND O.no = N.pedido
