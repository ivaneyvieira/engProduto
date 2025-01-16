DROP
TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE
TEMPORARY TABLE IF NOT EXISTS T_DADOS
SELECT S.no AS storeno,
       P.no AS prdno,
       IFNULL(B.grade, '') AS grade,
       0 AS vencimento,
       0 AS inventario,
       CURRENT_DATE * 1 AS dataEntrada
FROM sqldados.prd AS P
         LEFT JOIN sqldados.prdbar AS B
                   ON P.no = B.prdno
         LEFT JOIN sqldados.vend AS V
                   ON V.no = P.mfno
         INNER JOIN sqldados.store AS S
                    ON S.no IN (2, 3, 4, 5, 8)
                        AND (S.no = :loja)
WHERE IF(tipoGarantia = 2, garantia, 0) > 0
  AND (TRIM(P.no) * 1 = :codigo)
  AND (grade = :grade OR :grade = '')
GROUP BY S.no, B.grade;

DELETE
FROM sqldados.dadosValidade
WHERE storeno = :loja
  AND (TRIM(prdno) * 1 = :codigo)
  AND inventario = 0;

INSERT INTO sqldados.dadosValidade(storeno, prdno, grade, vencimento, inventario, dataEntrada)
SELECT storeno, prdno, grade, vencimento, inventario, dataEntrada
FROM T_DADOS;

SELECT COUNT(*) AS quant
FROM T_DADOS