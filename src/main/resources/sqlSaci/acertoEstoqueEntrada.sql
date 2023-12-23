SELECT N.storeno                                    AS loja,
       N.invno                                      AS ni,
       CONCAT(N.nfname, '/', N.invse)               AS notaFiscal,
       CAST(N.issue_date AS DATE)                   AS dataEmissao,
       N.vendno                                     AS fornecedor,
       V.name                                       AS nomeFornecedor,
       N.remarks                                    AS observacao,
       TRIM(I.prdno)                                AS codigoProduto,
       TRIM(MID(P.name, 1, 37))                     AS nomeProduto,
       I.grade                                      AS grade,
       ROUND(I.qtty / 1000)                         AS quantidade,
       ROUND(I.fob4 / 10000, 2)                     AS valorUnitario,
       ROUND((I.fob4 / 10000) * (I.qtty / 1000), 2) AS valorTotal
FROM sqldados.inv AS N
       LEFT JOIN sqldados.vend AS V
                 ON (V.no = N.vendno)
       LEFT JOIN sqldados.iprd AS I
                 USING (invno)
       LEFT JOIN sqldados.prd AS P
                 ON (P.no = I.prdno)
WHERE N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8)
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.issue_date >= :dataInicial OR :dataInicial = 0)
  AND (N.issue_date <= :dataFinal OR :dataFinal = 0)
  AND (N.remarks NOT LIKE '%CONSUMO%')
  AND N.cfo IN (1949)
  AND NOT (I.bits & POW(2, 4))
  AND V.sname LIKE 'ENG%'
