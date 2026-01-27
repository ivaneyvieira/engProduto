USE sqldados;

SET SQL_MODE = '';

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE TEMPORARY TABLE T_PEDIDO
SELECT O.storeno                          AS loja,
       O.no                               AS pedido,
       IF(O.no >= 20000,
          MID(O.no, 1, 1) * 1, O.storeno) AS lojaRessuprimento,
       CAST(O.date AS date)               AS data,
       O.vendno                           AS codFornecedor,
       ROUND(O.amt / 100, 2)              AS totalPedido,
       O.remarks                          AS observacao,
       I.prdno                            AS prdno,
       TRIM(I.prdno) * 1                  AS codigo,
       TRIM(MID(P.name, 1, 37))           AS descricao,
       I.grade                            AS grade,
       I.seqno                            AS seqno,
       ROUND(I.qttyVendaMes)              AS qttyVendaMes,
       ROUND(I.qttyVendaMesAnt)           AS qttyVendaMesAnt,
       ROUND(I.qttyVendaMedia, 2)         AS qttyVendaMedia,
       ROUND(I.qttyAbc, 0)                AS qttySugerida,
       ROUND(I.qtty, 0)                   AS qttyPedida
FROM
  sqldados.oprd              AS I
    INNER JOIN sqldados.ords AS O
               ON O.storeno = I.storeno AND O.no = I.ordno
    INNER JOIN sqldados.prd  AS P
               ON P.no = I.prdno
WHERE I.storeno = 1
  AND (O.no IN (20000, 30000, 40000, 50000, 80000, 3))
  AND (IF(O.no >= 20000,
          MID(O.no, 1, 1) * 1, O.storeno) = :loja OR :loja = 0)
  AND (O.date >= :dataInicial OR :dataInicial = 0)
  AND (O.date <= :dataFinal OR :dataFinal = 0)
  AND (:pesquisa = '' OR I.ordno = :pesquisa OR O.vendno = :pesquisa OR O.remarks LIKE CONCAT('%', :pesquisa, '%'));

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT DISTINCT prdno, grade
FROM
  T_PEDIDO;

DROP TEMPORARY TABLE IF EXISTS T_ESTOQUE;
CREATE TEMPORARY TABLE T_ESTOQUE
(
  PRIMARY KEY (prdno, grade)
)
SELECT S.prdno                                                                 AS prdno,
       S.grade                                                                 AS grade,
       ROUND(SUM(S.qtty_atacado + S.qtty_varejo) / 1000)                       AS estoque,
       ROUND(SUM(IF(S.storeno = 4, S.qtty_atacado + S.qtty_varejo, 0)) / 1000) AS estoqueLJ
FROM
  sqldados.stk       AS S
    INNER JOIN T_PRD AS P
               ON S.prdno = P.prdno AND S.grade = P.grade AND S.storeno IN (2, 3, 4, 5, 8)
GROUP BY prdno, grade;

SELECT P.loja,
       P.pedido,
       P.lojaRessuprimento,
       P.data,
       P.codFornecedor,
       P.totalPedido,
       P.observacao,
       P.prdno,
       P.codigo,
       P.descricao,
       P.grade,
       P.seqno,
       P.qttyVendaMes,
       P.qttyVendaMesAnt,
       E.estoque,
       P.qttyVendaMedia,
       P.qttySugerida,
       P.qttyPedida,
       E.estoqueLJ
FROM
  T_PEDIDO              AS P
    LEFT JOIN T_ESTOQUE AS E
              USING (prdno, grade)