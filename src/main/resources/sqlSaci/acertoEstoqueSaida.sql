SET
sql_mode = '';

DO
@PESQUISA := TRIM(:pesquisa);
DO
@PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO
@PESQUISASTART := CONCAT(@PESQUISA, '%');
DO
@PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

SELECT nf.storeno AS loja,
       CONCAT(nf.nfno, '/', nf.nfse) AS notaFiscal,
       CAST(nf.issuedate AS DATE) AS dataEmissao,
       nf.custno AS cliente,
       C.name AS nomeCliente,
       nf.remarks AS observacao,
       ROUND(nf.grossamt / 100, 2) AS valorNota,
       TRIM(I.prdno) AS codigoProduto,
       TRIM(MID(P.name, 1, 37)) AS nomeProduto,
       I.grade AS grade,
       IFNULL(R.form_label, '') AS rotulo,
       P.taxno AS tributacao,
       ROUND(I.qtty / 1000) AS quantidade,
       ROUND(I.preco, 2) AS valorUnitario,
       ROUND((I.preco) * (I.qtty / 1000), 2) AS valorTotal
FROM sqldados.nf
         LEFT JOIN sqldados.xaprd2 AS I
                   USING (storeno, pdvno, xano)
         LEFT JOIN sqldados.custp AS C
                   ON nf.custno = C.no
         LEFT JOIN sqldados.prd AS P
                   ON (P.no = I.prdno)
         LEFT JOIN sqldados.prdalq AS R
                   ON R.prdno = I.prdno
WHERE nf.storeno IN (1, 2, 3, 4, 5, 6, 7, 8)
  AND (nf.storeno = :loja OR :loja = 0)
  AND (nf.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (nf.issuedate <= :dataFinal OR :dataFinal = 0)
  AND nf.cfo = 5949
  AND nf.icms_amt = 0
  AND nf.grossamt > 0
  AND nf.status != 1
  AND nf.pdvno = 0
  AND nf.tipo = 7
HAVING (@PESQUISA = ''
    OR
    notaFiscal LIKE @PESQUISASTART
    OR
    cliente = @PESQUISANUM
    OR
    nomeCliente LIKE @PESQUISALIKE
    OR
    observacao LIKE @PESQUISALIKE
    )