USE sqldados;

SET sql_mode = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');


DROP TEMPORARY TABLE IF EXISTS T_MOVMANUAL;
CREATE TEMPORARY TABLE T_MOVMANUAL
SELECT M.storeno                                      AS loja,
       M.xano                                         AS transacao,
       CAST(M.date AS DATE)                           AS data,
       TRIM(M.prdno) * 1                              AS codigoProduto,
       TRIM(MID(P.name, 1, 37))                       AS nomeProduto,
       M.prdno                                        AS prdno,
       M.grade                                        AS grade,
       SUBSTRING_INDEX(CASE
                         WHEN M.remarks LIKE '66:PED E%' THEN TRIM(MID(M.remarks, 9, 100))
                         WHEN M.remarks LIKE '66:PED S%' THEN TRIM(MID(M.remarks, 9, 100))
                         ELSE '0'
                       END, ' ', 1) * 1               AS pedido,
       CASE
         WHEN M.remarks LIKE '66:PED E%' THEN 'E'
         WHEN M.remarks LIKE '66:PED S%' THEN 'S'
         ELSE ''
       END                                            AS tipoPedido,
       M.remarks                                      AS observacao,
       IFNULL(R.form_label, '')                       AS rotulo,
       P.taxno                                        AS tributacao,
       ROUND(M.qtty / 1000)                           AS qtty,
       ROUND(S.qtty_varejo / 1000)                    AS estVarejo,
       ROUND(S.qtty_atacado / 1000)                   AS estAtacado,
       ROUND((S.qtty_varejo + S.qtty_atacado) / 1000) AS estTotal
FROM sqldados.stkmov AS M
       LEFT JOIN sqldados.stk AS S
                 USING (storeno, prdno, grade)
       LEFT JOIN sqldados.prd AS P
                 ON (P.no = M.prdno)
       LEFT JOIN sqldados.prdalq AS R
                 ON R.prdno = M.prdno
WHERE M.storeno IN (1, 2, 3, 4, 5, 6, 7, 8)
  AND (M.storeno = :loja OR :loja = 0)
  AND (M.date >= :dataInicial OR :dataInicial = 0)
  AND (M.date <= :dataFinal OR :dataFinal = 0)
  AND (M.remarks LIKE '66:PED E%' OR M.remarks LIKE '66:PED S%')
  AND CASE :tipo
        WHEN 'E' THEN M.qtty > 0
        WHEN 'S' THEN M.qtty < 0
        WHEN 'T' THEN TRUE
      END;

SELECT loja,
       transacao,
       data,
       codigoProduto,
       nomeProduto,
       grade,
       pedido,
       observacao,
       rotulo,
       tributacao,
       qtty,
       estVarejo,
       estAtacado,
       estTotal,
       TRIM(IFNULL(SUBSTRING_INDEX(SUBSTRING_INDEX(MID(PS.remarks__480, 1, 40), '(', 1), '.', 1), '')) AS tipo
FROM T_MOVMANUAL AS M
       LEFT JOIN sqldados.eordrk AS PS
                 ON PS.storeno = M.loja
                   AND PS.ordno = M.pedido
WHERE (@PESQUISA = '' OR
       transacao = @PESQUISANUM OR
       codigoProduto = @PESQUISANUM OR
       nomeProduto LIKE @PESQUISALIKE OR
       grade LIKE @PESQUISASTART OR
       rotulo LIKE @PESQUISASTART OR
       tributacao LIKE @PESQUISASTART OR
       TRIM(IFNULL(SUBSTRING_INDEX(SUBSTRING_INDEX(MID(PS.remarks__480, 1, 40), '(', 1), '.', 1), '')) LIKE @PESQUISASTART
        )
