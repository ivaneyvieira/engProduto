SET SQL_MODE = '';

USE sqldados;

DROP TABLE IF EXISTS T_EMAIL_REP;
CREATE TEMPORARY TABLE T_EMAIL_REP
SELECT V.vendno, 'R' AS tipo, MIN(R.no) AS repno, R.email
FROM
  sqldados.rep                 AS R
    INNER JOIN sqldados.repven AS V
               ON V.repno = R.no
WHERE TRIM(R.email) != ''
GROUP BY V.vendno, R.email;

DROP TABLE IF EXISTS T_EMAIL_ADD;
CREATE TEMPORARY TABLE T_EMAIL_ADD
SELECT V.vendno, 'A' AS tipo, A.repno, emailList AS email
FROM
  sqldados.repAdicional        AS A
    INNER JOIN sqldados.repven AS V
               ON V.repno = A.repno
WHERE TRIM(A.emailList) != '';

DROP TABLE IF EXISTS T_EMAIL_UNION;
CREATE TEMPORARY TABLE T_EMAIL_UNION
(
  PRIMARY KEY (vendno)
)
SELECT vendno, GROUP_CONCAT(DISTINCT email ORDER BY tipo, repno) AS emailList
FROM
  ( SELECT vendno, tipo, repno, email FROM T_EMAIL_ADD UNION SELECT vendno, tipo, repno, email FROM T_EMAIL_REP ) AS D
GROUP BY vendno;

DROP TABLE IF EXISTS T_FORN;
CREATE TEMPORARY TABLE T_FORN
SELECT V.no                     AS no,
       C.no                     AS custno,
       V.name                   AS descricao,
       V.cgc                    AS cnpjCpf,
       V.fabOufor               AS classe,
       CASE V.fabOufor
         WHEN 0 THEN 'Fabricante'
         WHEN 1 THEN 'Fornecedor'
         WHEN 2 THEN 'Fab/Fornecedor'
                ELSE 'Desconhecido'
       END                      AS classificacao,
       A.termDev                AS termDev,
       COUNT(DISTINCT F.seq)    AS countArq,
       A.obs                    AS obs,
       IFNULL(EU.emailList, '') AS emailList
FROM
  sqldados.vend                    AS V
    LEFT JOIN sqldados.custp       AS C
              ON C.cpf_cgc = V.cgc
    LEFT JOIN vendAdicional        AS A
              ON A.vendno = V.no
    LEFT JOIN sqldados.vendArquivo AS F
              ON F.vendno = V.no
    LEFT JOIN T_EMAIL_UNION        AS EU
              ON EU.vendno = V.no
WHERE V.fabOufor IN (0, 1, 2)
GROUP BY V.no, V.name, V.cgc
HAVING :pesquisa = ''
    OR no = :pesquisa
    OR V.name LIKE CONCAT('%', :pesquisa, '%')
    OR V.cgc = :pesquisa
    OR classificacao LIKE CONCAT('%', :pesquisa, '%')
ORDER BY V.no;

SELECT no, custno, descricao, cnpjCpf, classe, classificacao, termDev, countArq, obs, emailList
FROM T_FORN
WHERE descricao NOT LIKE 'ENGECOPI%'

