USE
sqldados;

SET
SQL_MODE = '';

DO
@PESQUISA := :pesquisa;
DO
@PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO
@PESQUISA_START := CONCAT(:pesquisa, '%');
DO
@PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP
TEMPORARY TABLE IF EXISTS T_LOC;
CREATE
TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno                        AS prdno,
       A.grade                        AS grade,
       TRIM(MID(A.localizacao, 1, 4)) AS localizacao
FROM sqldados.prdAdicional AS A
WHERE ((TRIM(MID(A.localizacao, 1, 4)) IN (:local)) OR ('TODOS' IN (:local)) OR (A.localizacao = ''))
  AND (A.storeno = 4)
  AND (A.prdno = :prdno OR :prdno = '')
  AND (A.grade = :grade OR :grade = '');

SELECT O.storeno                               AS loja,
       O.ordno                                 AS numero,
       CAST(O.date AS DATE)                    AS data,
       IFNULL(L.localizacao, '')               AS localizacao,
       IFNULL(OA.observacao, '')               AS observacao,
       IFNULL(EE.no, 0)                        AS entregueNo,
       IFNULL(EE.name, '')                     AS entregueNome,
       IFNULL(EE.login, '')                    AS entregueSNome,
       IFNULL(EF.no, 0)                        AS finalizadoNo,
       IFNULL(EF.name, '')                     AS finalizadoNome,
       IFNULL(EF.login, '')                    AS finalizadoSNome,
       IFNULL(ER.no, 0)                        AS recebidoNo,
       IFNULL(ER.name, '')                     AS recebidoNome,
       IFNULL(ER.sname, '')                    AS recebidoSNome,
       E.prdno                                 AS prdno,
       TRIM(E.prdno)                           AS codigo,
       E.grade                                 AS grade,
       IFNULL(EA.marca, 0)                     AS marca,
       TRIM(IFNULL(B.barcode, P.barcode))      AS barcode,
       TRIM(MID(P.name, 1, 37))                AS descricao,
       ROUND(E.qtty / 1000)                    AS quantidade,
       IFNULL(EA.qtRecebido, 0)                AS qtRecebido,
       EA.selecionado                          AS selecionado,
       EA.posicao                              AS posicao,
       (S.qtty_atacado + S.qtty_varejo) / 1000 AS qtEstoque,
       O.paymno                                AS metodo,
       IF(O.paymno = 433,
          ROUND(CASE
                    WHEN R.remarks__480 LIKE 'ENTRADA%' THEN 1
                    WHEN R.remarks__480 LIKE 'SAIDA%' THEN -1
                    ELSE 0
              END), 1)                         AS multAcerto
FROM sqldados.eoprd AS E
         LEFT JOIN T_LOC AS L
                   USING (prdno, grade)
         LEFT JOIN sqldados.eoprdAdicional AS EA
                   USING (storeno, ordno, prdno, grade)
         INNER JOIN sqldados.eord AS O
                    USING (storeno, ordno)
         LEFT JOIN sqldados.eordrk AS R
                   USING (storeno, ordno)
         LEFT JOIN sqldados.eordAdicional AS OA
                   ON OA.storeno = O.storeno
                       AND OA.ordno = O.ordno
                       AND OA.localizacao != ''
                   AND OA.localizacao = L.localizacao
       LEFT JOIN sqldados.stk AS S
ON S.prdno = E.prdno
    AND S.grade = E.grade
    AND S.storeno = E.storeno
    LEFT JOIN sqldados.prdbar AS B
    ON B.prdno = E.prdno
    AND B.grade = E.grade
    AND B.grade != ''
    LEFT JOIN sqldados.users AS EE
    ON EE.no = EA.empEntregue
    LEFT JOIN sqldados.users AS EF
    ON EF.no = EA.empFinalizado
    LEFT JOIN sqldados.emp AS ER
    ON ER.no = EA.empRecebido
    INNER JOIN sqldados.prd AS P
    ON P.no = E.prdno
WHERE (O.paymno IN (431
    , 432
    , 433))
  AND (O.paymno = :metodo
   OR :metodo = 0)
  AND (O.date >= :datacorte)
  AND (O.date >= SUBDATE(CURDATE()
    , INTERVAL 60 YEAR))
  AND (O.date >= :dataInicial
   OR :dataInicial = 0)
  AND (O.date <= :dataFinal
   OR :dataFinal = 0)
  AND (O.storeno = :loja
   OR :loja = 0)
  AND (E.prdno = :prdno
   OR :prdno = '')
  AND (E.grade = :grade
   OR :grade = '')
  AND (O.ordno = @PESQUISA_NUM
   OR
    IFNULL(L.localizacao
    , '') LIKE @PESQUISA_START
   OR
    IFNULL(OA.observacao
    , '') LIKE @PESQUISA_LIKE
   OR
    IFNULL(EE.name
    , '') LIKE @PESQUISA_LIKE
   OR
    IFNULL(ER.name
    , '') LIKE @PESQUISA_LIKE
   OR
    @PESQUISA = '')
GROUP BY E.storeno, E.ordno, E.prdno, E.grade
HAVING multAcerto != 0
