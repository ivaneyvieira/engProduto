USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT no                                 AS prdno,
       IFNULL(grade, '')                  AS grade,
       TRIM(IFNULL(B.barcode, P.barcode)) AS codigoBarras,
       TRIM(MID(P.name, 1, 37))           AS descricao
FROM
  sqldados.prd                AS P
    LEFT JOIN sqldados.prdbar AS B
              ON P.no = B.prdno
WHERE P.no = LPAD(:codigo, 16, ' ')
GROUP BY P.no, IFNULL(B.grade, '');

SELECT TRIM(P.prdno)                                             AS codigo,
       P.prdno                                                   AS prdno,
       P.codigoBarras                                            AS codigoBarras,
       P.descricao                                               AS descricao,
       P.grade                                                   AS grade,
       ROUND(IFNULL((S.qtty_varejo + S.qtty_atacado) / 1000, 0)) AS saldo
FROM
  T_PRD                    AS P
    LEFT JOIN sqldados.stk AS S
              ON S.prdno = P.prdno
                AND S.grade = P.grade
                AND S.storeno = :loja
GROUP BY prdno, grade
