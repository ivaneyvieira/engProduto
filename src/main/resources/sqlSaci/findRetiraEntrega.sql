USE
sqldados;

DROP
TEMPORARY TABLE IF EXISTS T_E;
CREATE
TEMPORARY TABLE T_E
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
       P.eordno AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero
FROM sqlpdv.pxa AS P
WHERE P.cfo IN (5117, 6117)
  AND storeno IN (2, 3, 4, 5)
  AND date >= 20190101
GROUP BY storeno, ordno;

DROP
TEMPORARY TABLE IF EXISTS T_V;
CREATE
TEMPORARY TABLE T_V
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
       P.eordno AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       nfno,
       nfse
FROM sqlpdv.pxa AS P
WHERE P.cfo IN (5922, 6922)
  AND storeno IN (2, 3, 4, 5)
  AND nfse = '1'
  AND date >= 20190101
GROUP BY storeno, ordno;

SELECT E.storeno AS loja,
       E.ordno AS pedido,
       CAST(EO.date AS DATE) AS data,
       CAST(IFNULL(T_V.numero, '') AS CHAR) AS nota,
       IF(E.bits & POW(2, 1), 'RETIRA', 'CD') AS tipo,
       EO.custno AS cliente,
       P.mfno AS vendno,
       CAST(TRIM(P.no) AS CHAR) AS codigo,
       EO.empno AS empno,
       TRIM(MID(P.name, 1, 37)) AS descricao,
       E.grade AS grade,
       E.qtty / 1000 AS quant,
       IFNULL(S.qtty_varejo / 1000, 0) AS estSaci,
       IFNULL(S.qtty_varejo / 1000, 0) - E.qtty / 1000 AS saldo,
       P.typeno AS typeno,
       IFNULL(T.name, '') AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR) AS clno,
       IFNULL(cl.name, '') AS clname,
       MID(IFNULL(L.localizacao, ''), 1, 4) AS localizacao,
       E.remarks AS gradeAlternativa
FROM sqldados.eoprdf AS E
         INNER JOIN sqldados.eord AS EO
                    USING (storeno, ordno)
         INNER JOIN T_V
                    USING (storeno, ordno)
         LEFT JOIN T_E
                   USING (storeno, ordno)
         LEFT JOIN sqldados.stk AS S
                   ON E.prdno = S.prdno AND E.grade = S.grade AND S.storeno = 4
         LEFT JOIN sqldados.prdloc AS L
                   ON L.prdno = S.prdno AND L.grade = E.grade AND L.storeno = 4
         LEFT JOIN sqldados.prd AS P
                   ON E.prdno = P.no
         LEFT JOIN sqldados.vend AS F
                   ON F.no = P.mfno
         LEFT JOIN sqldados.type AS T
                   ON T.no = P.typeno
         LEFT JOIN sqldados.cl
                   ON cl.no = P.clno
WHERE (E.prdno = :prdno OR :prdno = '')
  AND (P.typeno = :typeno OR :typeno = 0)
  AND (P.clno = :clno OR P.deptno = :clno OR P.groupno = :clno OR :clno = 0)
  AND (P.mfno = :vendno OR :vendno = 0)
  AND (T_V.nfno = :nfno OR :nfno = 0)
  AND (T_V.nfse = :nfse OR :nfse = '')
  AND T_E.numero IS NULL
  AND EO.date >= 20190101
  AND (L.localizacao LIKE CONCAT(:localizacao, '%') OR :localizacao = '')
  AND (E.storeno = :loja OR :loja = 0)
  AND (E.remarks != '' OR :isEdit = 'N')
GROUP BY codigo, grade

