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

*/

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, 0);

SELECT N.storeno                                                                  AS loja,
       N.pdvno                                                                    AS pdv,
       N.xano                                                                     AS transacao,
       CONCAT(N.nfno, '/', N.nfse)                                                AS nfVenda,
       CAST(N.issuedate AS DATE)                                                  AS dataEmissao,
       N.custno                                                                   AS codCliente,
       C.name                                                                     AS nomeCliente,
       N.grossamt / 100                                                           AS valorVenda,
       A.tipoDev                                                                  AS tipoDev,
       S.no                                                                       AS usernoSing,
       S.name                                                                     AS autorizacao,
       IFNULL(I1.invno, I2.invno)                                                 AS ni,
       IFNULL(CONCAT(I1.nfname, '/', I1.invse), CONCAT(I2.nfname, '/', I2.invse)) AS nfDev,
       CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE)                         AS dataDev,
       IFNULL(I1.grossamt, I2.grossamt) / 100                                     AS valorDev,
       U.name                                                                     AS usuarioDev,
       U.login                                                                    AS loginDev,
       A.observacao                                                               AS observacao
FROM sqldados.nf AS N
       INNER JOIN sqldados.nfAutorizacao AS A
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
       LEFT JOIN sqldados.inv AS I1
                 ON (N.nfno = I1.nfNfno AND N.storeno = I1.nfStoreno AND N.nfse = I1.nfNfse)
       LEFT JOIN sqldados.inv AS I2
                 ON (N.storeno = I2.s1 AND N.pdvno = I2.s2 AND N.xano = I2.l2)
       LEFT JOIN sqldados.users AS U
                 ON U.no = IFNULL(IF(I1.usernoLast = 0, I1.usernoFirst, I1.usernoLast),
                                  IF(I2.usernoLast = 0, I2.usernoFirst, I2.usernoLast))
       LEFT JOIN sqldados.users AS S
                 ON S.no = A.usernoSing
WHERE (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND (@PESQUISA = '' OR
       CONCAT(N.nfno, '/', N.nfse) LIKE @PESQUISA_START OR
       N.custno LIKE @PESQUISA_INT OR
       C.name LIKE @PESQUISA_LIKE OR
       A.tipoDev LIKE @PESQUISA_LIKE OR
       S.name LIKE @PESQUISA_LIKE OR
       A.observacao LIKE @PESQUISA_LIKE)
HAVING (@PESQUISA = '' OR
        ni = @PESQUISA_INT OR
        nfDev LIKE @PESQUISA_START)
