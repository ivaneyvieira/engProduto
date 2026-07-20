USE sqldados;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);



DROP TABLE IF EXISTS T_CLI;
CREATE TEMPORARY TABLE T_CLI
SELECT C.no                                             AS custno,
       C.name                                           AS nome,
       C.cpf_cgc                                        AS cpfCnpj,
       IF(C.fjflag = 8, 'J', 'F')                       AS tipoPessoa,
       CASE rating
         WHEN 0 THEN 'A'
         WHEN 1 THEN 'B'
         WHEN 2 THEN 'C'
         WHEN 3 THEN 'D'
                ELSE ''
       END                                              AS tipoCliente,
       CAST(IF(dtcrlimit = 0, NULL, dtcrlimit) AS date) AS dataCredito,
       crlimit / 100                                    AS limiteCredito,
       CAST(IF(purlastdt = 0, NULL, purlastdt) AS date) AS ultCompra,
       (crlimit / 100)                                  AS valorLimite
FROM
  sqldados.custp                AS C
    LEFT JOIN sqldados.rotasAdd AS R
              ON C.city1 = R.cidade AND (C.nei1 = R.bairro)
WHERE (@PESQUISA = '' OR C.no = @PESQUISA_NUM OR C.name LIKE @PESQUISA_LIKE OR C.cpf_cgc LIKE @PESQUISA_LIKE OR
       IF(C.fjflag = 1, id_sname, '') LIKE @PESQUISA_LIKE)
  AND (CASE :operacao
         WHEN '=' THEN crlimit = ROUND(:credito * 100)
         WHEN '<' THEN crlimit < ROUND(:credito * 100)
         WHEN '>' THEN crlimit > ROUND(:credito * 100)
         WHEN 'T' THEN TRUE
       END);

DROP TABLE IF EXISTS T_DUP;
CREATE TEMPORARY TABLE T_DUP
(
  PRIMARY KEY (custno)
)
SELECT custno, SUM(amtdue / 100) AS valorDevido, SUM(IF(duedate < CURDATE(), amtdue, 0.00) / 100) AS valorAtrasado
FROM
  sqldados.dup       AS D
    INNER JOIN T_CLI AS C
               USING (custno)
WHERE status IN (0, 1, 8)
GROUP BY custno;

DROP TABLE IF EXISTS T_CLI_CREDITO;
CREATE TEMPORARY TABLE T_CLI_CREDITO
SELECT custno                                  AS custno,
       nome                                    AS nome,
       cpfCnpj                                 AS cpfCnpj,
       tipoPessoa                              AS tipoPessoa,
       tipoCliente                             AS tipoCliente,
       dataCredito                             AS dataCredito,
       limiteCredito                           AS limiteCredito,
       ultCompra                               AS ultCompra,
       IFNULL(valorDevido, 0.00)               AS valorAberto,
       IFNULL(valorAtrasado, 0.00)             AS valorAtrasado,
       valorLimite - IFNULL(valorDevido, 0.00) AS valorDisponivel
FROM
  T_CLI             AS C
    LEFT JOIN T_DUP AS D
              USING (custno);

SELECT custno          AS custno,
       nome            AS nome,
       cpfCnpj         AS cpfCnpj,
       tipoPessoa      AS tipoPessoa,
       tipoCliente     AS tipoCliente,
       dataCredito     AS dataCredito,
       limiteCredito   AS limiteCredito,
       ultCompra       AS ultCompra,
       valorAberto     AS valorAberto,
       valorAtrasado   AS valorAtrasado,
       valorDisponivel AS valorDisponivel
FROM T_CLI_CREDITO
ORDER BY custno
