SELECT N.storeno                                   AS loja,
       N.invno                                     AS ni,
       N.nfname                                    AS numero,
       N.invse                                     AS serie,
       CAST(N.issue_date AS DATE)                  AS dataEmissao,
       CAST(N.date AS DATE)                        AS dataEntrada,
       N.vendno                                    AS fornecedorNota,
       GROUP_CONCAT(DISTINCT P.mfno ORDER BY mfno) AS fornecedorCad,
       V.cgc                                       AS cnpjEmitente,
       V.name                                      AS nomeFornecedor,
       N.prdamt / 100                              AS valorTotalProdutos,
       N.grossamt / 100                            AS valorTotal,
       K.nfekey                                    AS chave,
       N.cfo                                       AS cfop,
       N.auxLong2                                  AS cte
FROM sqldados.inv AS N
       LEFT JOIN sqldados.iprd AS I
                 USING (invno)
       LEFT JOIN sqldados.prd AS P
                 ON P.no = I.prdno
       INNER JOIN sqldados.vend AS V
                  ON V.no = N.vendno
       INNER JOIN sqldados.invnfe AS K
                  USING (invno)
WHERE (V.cgc NOT LIKE '07.483.654%')
  AND (N.bits & POW(2, 4) = 0)
  AND (N.type = 0)
  AND (N.date BETWEEN :dataInicial AND :dataFinal)
  AND (N.nfname = :numero OR :numero = 0)
  AND (V.cgc = :cnpj OR :cnpj = '')
  AND (N.storeno = :loja OR :loja = 0)
  AND (V.name LIKE CONCAT(:fornecedor, '%') OR (P.mfno = :fornecedor OR P.mfno IS NULL) OR
       (N.vendno = :fornecedor) OR :fornecedor = '')
GROUP BY N.invno
