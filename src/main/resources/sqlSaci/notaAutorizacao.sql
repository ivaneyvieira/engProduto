USE sqldados;

/*
CREATE TABLE sqldados.nfAutorizacao
(
  storeno INT,
  pdvno   INT,
  xano    INT,
  PRIMARY KEY (storeno, pdvno, xano)
)

alter table sqldados.nfAutorizacao
  add column usernoSing int after xano,
  add column tipoDev varchar(20) after usernoSing

alter table sqldados.nfAutorizacao
  add column observacao varchar(100) after tipoDev

alter table sqldados.nfAutorizacao
  add column impresso varchar(1) default 'N' after observacao

alter table sqldados.nfAutorizacao
  add column dataInsert int default '0' after impresso

alter table sqldados.nfAutorizacao
  add column autoriza varchar(1) default 'N';

alter table sqldados.nfAutorizacao
  add column solicitacaoTroca varchar(1) default '';

alter table sqldados.nfAutorizacao
  add column produtoTroca varchar(1) default '';


alter table sqldados.nfAutorizacao
  add column userSolicitacao int default 0;


alter table sqldados.nfAutorizacao
  add column userTroca int default 0;

alter table sqldados.nfAutorizacao
  add column motivoTroca text;

*/

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, 0);

SELECT N.storeno                                                                                  AS loja,
       N.pdvno                                                                                    AS pdv,
       N.xano                                                                                     AS transacao,
       CONCAT(N.nfno, '/', N.nfse)                                                                AS nfVenda,
       CAST(N.issuedate AS DATE)                                                                  AS dataEmissao,
       N.custno                                                                                   AS codCliente,
       C.name                                                                                     AS nomeCliente,
       N.grossamt / 100                                                                           AS valorVenda,
       A.tipoDev                                                                                  AS tipoDev,
       S.no                                                                                       AS usernoSing,
       S.name                                                                                     AS autorizacao,
       IFNULL(I1.invno, I2.invno)                                                                 AS ni,
       IFNULL(CONCAT(I1.nfname, '/', I1.invse), CONCAT(I2.nfname, '/', I2.invse))                 AS nfDev,
       CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE)                                         AS dataDev,
       IFNULL(I1.grossamt, I2.grossamt) / 100                                                     AS valorDev,
       U.name                                                                                     AS usuarioDev,
       U.login                                                                                    AS loginDev,
       A.observacao                                                                               AS observacao,
       A.impresso                                                                                 AS impresso,
       IF(A.dataInsert = 0, CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE),
          IFNULL(CAST(A.dataInsert AS DATE), CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE))) AS data
FROM
  sqldados.nf                         AS N
    INNER JOIN sqldados.nfAutorizacao AS A
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.custp         AS C
               ON C.no = N.custno
    LEFT JOIN  sqldados.inv           AS I1
               ON (N.nfno = I1.nfNfno AND N.storeno = I1.nfStoreno AND N.nfse = I1.nfNfse)
    LEFT JOIN  sqldados.inv           AS I2
               ON (N.storeno = I2.s1 AND N.pdvno = I2.s2 AND N.xano = I2.l2)
    LEFT JOIN  sqldados.users         AS U
               ON U.no = IFNULL(IF(I1.usernoFirst = 0, I1.usernoLast, I1.usernoFirst),
                                IF(I2.usernoFirst = 0, I2.usernoLast, I2.usernoFirst))
    LEFT JOIN  sqldados.users         AS S
               ON S.no = A.usernoSing
WHERE (N.storeno = :loja OR :loja = 0)
  AND (IF(A.dataInsert = 0, CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE),
          IFNULL(CAST(A.dataInsert AS DATE), CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE))) * 1 >= :dataInicial OR
       :dataInicial = 0)
  AND (IF(A.dataInsert = 0, CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE),
          IFNULL(CAST(A.dataInsert AS DATE), CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE))) * 1 <= :dataFinal OR
       :dataFinal = 0)
  AND (@PESQUISA = '' OR CONCAT(N.nfno, '/', N.nfse) LIKE @PESQUISA_START OR N.custno LIKE @PESQUISA_INT OR
       C.name LIKE @PESQUISA_LIKE OR A.tipoDev LIKE @PESQUISA_LIKE OR S.name LIKE @PESQUISA_LIKE OR
       A.observacao LIKE @PESQUISA_LIKE OR IFNULL(I1.invno, I2.invno) = @PESQUISA_INT OR
       IFNULL(CONCAT(I1.nfname, '/', I1.invse), CONCAT(I2.nfname, '/', I2.invse)) LIKE @PESQUISA_START)
  AND (IFNULL(I1.remarks, I2.remarks) NOT LIKE '% P' AND IFNULL(I1.remarks, I2.remarks) NOT LIKE '% P %')
