USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT S.prdno                                               AS prdno,
       S.grade                                               AS grade,
       COALESCE(A.localizacao, MID(L.localizacao, 1, 4), '') AS localizacao
FROM sqldados.stk AS S
       LEFT JOIN sqldados.prdloc AS L
                 ON S.storeno = L.storeno
                   AND S.prdno = L.prdno
                   AND S.grade = L.grade
       LEFT JOIN sqldados.prdAdicional AS A
                 ON S.storeno = A.storeno
                   AND S.prdno = A.prdno
                   AND S.grade = A.grade
                   AND A.localizacao != ''
WHERE S.storeno = 4
GROUP BY S.storeno, S.prdno, S.grade;

SELECT O.storeno                          AS loja,
       O.ordno                            AS numero,
       CAST(O.date AS DATE)               AS data,
       IFNULL(L.localizacao, '')          AS localizacao,
       IFNULL(OA.observacao, '')          AS observacao,
       IFNULL(EE.no, 0)                   AS entregueNo,
       IFNULL(EE.name, '')                AS entregueNome,
       IFNULL(EE.login, '')               AS entregueSNome,
       IFNULL(ER.no, 0)                   AS recebidoNo,
       IFNULL(ER.name, '')                AS recebidoNome,
       IFNULL(ER.sname, '')               AS recebidoSNome,
       E.prdno                            AS prdno,
       TRIM(E.prdno)                      AS codigo,
       E.grade                            AS grade,
       IFNULL(EA.marca, 0)                AS marca,
       TRIM(IFNULL(B.barcode, P.barcode)) AS barcode,
       TRIM(MID(P.name, 1, 37))           AS descricao,
       ROUND(E.qtty / 1000)               AS quantidade,
       IFNULL(EA.qtRecebido, 0)           AS qtRecebido,
       EA.selecionado                     AS selecionado,
       EA.posicao                         AS posicao
FROM sqldados.eoprd AS E
       LEFT JOIN sqldados.eoprdAdicional AS EA
                 USING (storeno, ordno, prdno, grade)
       INNER JOIN sqldados.eord AS O
                  USING (storeno, ordno)
       LEFT JOIN T_LOC AS L
                 USING (prdno, grade)
       LEFT JOIN sqldados.eordAdicional AS OA
                 USING (storeno, ordno, localizacao)
       LEFT JOIN sqldados.prdbar AS B
                 USING (prdno, grade)
       LEFT JOIN sqldados.users AS EE
                 ON EE.no = OA.empEntregue
       LEFT JOIN sqldados.emp AS ER
                 ON ER.no = OA.empRecebido
       INNER JOIN sqldados.prd AS P
                  ON P.no = E.prdno
WHERE O.paymno = 431
  AND O.date >= :datacorte
  AND O.date >= SUBDATE(CURDATE(), INTERVAL 60 YEAR)
  AND (O.storeno = :loja OR :loja = 0)
  AND (L.localizacao in (:localizacao) OR 'TODOS' in (:localizacao) OR :localizacao = '')
  AND (O.ordno = @PESQUISA_NUM OR
       IFNULL(L.localizacao, '') LIKE @PESQUISA_START OR
       IFNULL(OA.observacao, '') LIKE @PESQUISA_LIKE OR
       IFNULL(EE.name, '') LIKE @PESQUISA_LIKE OR
       IFNULL(ER.name, '') LIKE @PESQUISA_LIKE OR
       @PESQUISA = '')
GROUP BY E.storeno, E.ordno, E.prdno, E.grade
