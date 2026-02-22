USE sqldados;

SET SQL_MODE = '';

DROP TEMPORARY TABLE IF EXISTS T_LOC_APP;
CREATE TEMPORARY TABLE T_LOC_APP
(
  PRIMARY KEY (prdno, grade)
)
SELECT storeno     AS loja,
       prdno       AS prdno,
       grade       AS grade,
       dataInicial AS dataInicial
FROM
  sqldados.prdAdicional
WHERE (storeno IN (2, 3, 4, 5, 8))
  AND (storeno = IF(:loja = 0, 4, :loja))
  AND (prdno = :prdno)
  AND (grade = :grade)
GROUP BY storeno, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS temp_pesquisa;
CREATE TEMPORARY TABLE temp_pesquisa
SELECT loja                                                                           AS loja,
       A.prdno                                                                        AS prdno,
       TRIM(A.prdno) * 1                                                              AS codigo,
       A.grade                                                                        AS grade,
       CAST(IF(IFNULL(A.dataInicial, 0) = 0, NULL, IFNULL(A.dataInicial, 0)) AS DATE) AS dataInicial
FROM
  T_LOC_APP AS A;

SELECT loja,
       NULL AS lojaSigla,
       prdno,
       codigo,
       NULL AS descricao,
       NULL AS unidade,
       grade,
       NULL AS tipo,
       NULL AS cl,
       NULL AS embalagem,
       NULL AS qtdEmbalagem,
       NULL AS estoque,
       NULL AS locNerus,
       NULL AS locApp,
       NULL AS codForn,
       NULL AS fornecedor,
       NULL AS fornecedorAbrev,
       NULL AS cnpjFornecedor,
       NULL AS saldo,
       NULL AS valorEstoque,
       NULL AS saldoVarejo,
       NULL AS saldoAtacado,
       dataInicial,
       NULL AS dataUpdate,
       NULL AS kardec,
       NULL AS dataConferencia,
       NULL AS qtConferencia,
       NULL AS qtConfEdit,
       NULL AS qtConfEditLoja,
       NULL AS preco,
       NULL AS estoqueUser,
       NULL AS estoqueLogin,
       NULL AS estoqueData,
       NULL AS estoqueCD,
       NULL AS estoqueLoja,
       NULL AS barcode,
       NULL AS ref,
       NULL AS numeroAcerto,
       NULL AS processado,
       NULL AS estoqueConfCD,
       NULL AS estoqueConfLoja,
       NULL AS vendaMesAnterior,
       NULL AS vendaMesAtual,
       NULL AS quantDevolucao
FROM
  temp_pesquisa
