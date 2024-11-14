SELECT N.id                                       AS id,
       L.no                                       AS loja,
       L.sname                                    AS sigla,
       NUMERO                                     AS numero,
       SERIE                                      AS serie,
       dataEmissao                                AS dataEmissao,
       NULL                                       AS dataEntrada,
       V.no                                       AS fornecedorNota,
       C.no                                       AS fornecedorCad,
       cnpjEmitente                               AS cnpjEmitente,
       nomeFornecedor                             AS nomeFornecedor,
       valorTotalProdutos                         AS valorTotalProdutos,
       MID(N.xmlNfe,
           LOCATE('<vNF>', N.xmlNfe) + 5,
           LOCATE('</vNF>', N.xmlNfe)
             - LOCATE('<vNF>', N.xmlNfe) - 5) * 1 AS valorTotal,
       MID(N.xmlNfe,
           LOCATE('<xPed>', N.xmlNfe) + 6,
           LOCATE('</xPed>', N.xmlNfe)
             - LOCATE('<xPed>', N.xmlNfe) - 6)    AS pedido,
       N.chave                                    AS chave,
       xmlNfe                                     AS xmlNfe,
       IF(I2.invno IS NULL, 'N', 'S')             AS preEntrada
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
  AND (N.dataEmissao BETWEEN :dataInicial AND :dataFinal)
  AND (N.NUMERO = :numero OR :numero = 0)
  AND (V.cgc = :cnpj OR :cnpj = '')
  AND (L.no = :loja OR :loja = 0)
  AND (V.name LIKE CONCAT(:fornecedor, '%') OR
       (V.no = :fornecedor) OR :fornecedor = '')
  AND ((:preEntrada = 'S' AND I2.invno IS NOT NULL) OR
       (:preEntrada = 'N' AND I2.invno IS NULL) OR
       (:preEntrada = 'T'))
ORDER BY dataEmissao
    DESC
