SET SQL_MODE = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
SELECT N.id                                              AS id,
       L.no                                              AS loja,
       L.sname                                           AS sigla,
       NUMERO                                            AS numero,
       SERIE                                             AS serie,
       dataEmissao                                       AS dataEmissao,
       V.no                                              AS fornecedorNota,
       C.no                                              AS fornecedorCad,
       cnpjEmitente                                      AS cnpjEmitente,
       nomeFornecedor                                    AS nomeFornecedor,
       valorTotalProdutos                                AS valorTotalProdutos,
       MID(N.xmlNfe, LOCATE('<vNF>', N.xmlNfe) + 5, LOCATE('</vNF>', N.xmlNfe) - LOCATE('<vNF>', N.xmlNfe) - 5) *
       1                                                 AS valorTotal,
       MID(N.xmlNfe, LOCATE('<natOp>', N.xmlNfe) + 7, LOCATE('</natOp>', N.xmlNfe) - LOCATE('<natOp>', N.xmlNfe) -
                                                      7) AS natureza,
       N.chave                                           AS chave,
       xmlNfe                                            AS xmlNfe,
       IF(I2.invno IS NULL, 'N', 'S')                    AS preEntrada,
       IF(I.invno IS NULL, 'N', 'S')                     AS entrada,
       I2.ordno                                          AS ordno,
       P.pedido                                          AS pedidoEdit
FROM
  sqldados.notasEntradaNdd     AS N
  INNER JOIN sqldados.store    AS L
    ON N.cnpjDestinatario = L.cgc
  LEFT JOIN sqldados.inv       AS I
    ON N.numero = I.nfname AND N.serie = I.invse AND L.no = I.storeno AND I.issue_date >= :dataInicial
  LEFT JOIN sqldados.inv2      AS I2
    ON N.numero = I2.nfname AND N.serie = I2.invse AND L.no = I2.storeno AND I2.issue_date >= :dataInicial
  LEFT JOIN sqldados.vend      AS V
    ON V.cgc = N.cnpjEmitente
  LEFT JOIN sqldados.custp     AS C
    ON C.cpf_cgc = N.cnpjEmitente
  LEFT JOIN sqldados.pedidoNdd AS P
    ON P.id = N.id
WHERE
    (N.cnpjEmitente NOT LIKE '07.483.654%')
AND (N.dataEmissao >= :dataInicial OR :dataInicial = 0)
AND (N.dataEmissao <= :dataFinal OR :dataFinal = 0)
AND (N.NUMERO = :numero OR :numero = 0)
AND (V.cgc = :cnpj OR :cnpj = '')
AND (L.no = :loja OR :loja = 0)
AND (V.name LIKE CONCAT(:fornecedor, '%') OR (V.no = :fornecedor) OR :fornecedor = '')
AND ((:entrada = 'S' AND I.invno IS NOT NULL) OR (:entrada = 'N' AND I.invno IS NULL) OR (:entrada = 'T'))
AND ((:preEntrada = 'S' AND I2.invno IS NOT NULL) OR (:preEntrada = 'N' AND I2.invno IS NULL) OR (:preEntrada = 'T'));

SELECT id,
       loja,
       sigla,
       numero,
       serie,
       natureza,
       dataEmissao,
       fornecedorNota,
       fornecedorCad,
       cnpjEmitente,
       nomeFornecedor,
       valorTotalProdutos,
       valorTotal,
       chave,
       xmlNfe,
       entrada,
       preEntrada,
       ordno,
       pedidoEdit
FROM
  T_NOTA AS N

