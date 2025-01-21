DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP TABLE IF EXISTS T_CLI;
CREATE TEMPORARY TABLE T_CLI
(
  custno     INT         NOT NULL,
  nome       CHAR(40)    NOT NULL,
  cpfCnpj    CHAR(20)    NOT NULL,
  tipoPessoa VARCHAR(1)  NOT NULL,
  rg         VARCHAR(20) NOT NULL,
  endereco   CHAR(60)    NOT NULL,
  bairro     CHAR(60)    NOT NULL,
  cidade     CHAR(60)    NOT NULL,
  estado     CHAR(2)     NOT NULL,
  ddd        INT,
  telefone   INT,
  celular    VARCHAR(11),
  PRIMARY KEY (custno)
)
SELECT C.no                                       AS custno,
       C.name                                     AS nome,
       C.cpf_cgc                                  AS cpfCnpj,
       IF(C.fjflag = 8, 'J', 'F')                 AS tipoPessoa,
       IF(C.fjflag = 1, id_sname, '')             AS rg,
       add1                                       AS endereco,
       nei1                                       AS bairro,
       city1                                      AS cidade,
       state1                                     AS estado,
       TRIM(MID(ddd, 1, 5)) * 1                   AS ddd,
       TRIM(MID(tel, 1, 10)) * 1                  AS telefone,
       IF(celular = 0, '', CAST(celular AS CHAR)) AS celular,
       IF(C.city1 = 'TIMON', 'Timon', R.rota)     AS rota
FROM
  sqldados.custp                AS C
    LEFT JOIN sqldados.rotasAdd AS R
              ON C.city1 = R.cidade AND (C.nei1 = R.bairro)
WHERE (@PESQUISA = '' OR C.no = @PESQUISA_NUM OR C.name LIKE @PESQUISA_LIKE OR C.cpf_cgc LIKE @PESQUISA_LIKE OR
       IF(C.fjflag = 1, id_sname, '') LIKE @PESQUISA_LIKE OR C.add1 LIKE @PESQUISA_LIKE OR C.nei1 LIKE @PESQUISA_LIKE OR
       C.city1 LIKE @PESQUISA_LIKE OR C.state1 LIKE @PESQUISA_START OR ddd LIKE @PESQUISA_START OR
       TRIM(MID(tel, 1, 10)) LIKE @PESQUISA_START OR IF(celular = 0, '', CAST(celular AS CHAR)) LIKE @PESQUISA_LIKE OR
       R.rota LIKE @PESQUISA_LIKE);


SELECT custno                                       AS custno,
       nome                                         AS nome,
       cpfCnpj                                      AS cpfCnpj,
       tipoPessoa                                   AS tipoPessoa,
       rg                                           AS rg,
       endereco                                     AS endereco,
       bairro                                       AS bairro,
       cidade                                       AS cidade,
       estado                                       AS estado,
       IF(ddd = 0, '', CAST(ddd AS CHAR))           AS ddd,
       IF(telefone = 0, '', CAST(telefone AS CHAR)) AS telefone,
       celular                                      AS celular,
       rota                                         AS rota
FROM
  T_CLI
