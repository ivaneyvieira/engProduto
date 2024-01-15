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

SELECT N.storeno                      AS loja,
       N.pdvno                        AS pdv,
       N.xano                         AS transacao,
       CONCAT(N.nfno, '/', N.nfse)    AS nfVenda,
       CAST(N.issuedate AS DATE)      AS dataEmissao,
       N.custno                       AS codCliente,
       C.name                         AS nomeCliente,
       N.grossamt / 100               AS valorVenda,
       A.tipoDev                      AS tipoDev,
       S.no                           AS usernoSing,
       S.name                         AS autorizacao,
       I.invno                        AS ni,
       CONCAT(I.nfname, '/', I.invse) AS nfDev,
       CAST(I.issue_date AS DATE)     AS dataDev,
       I.grossamt / 100               AS valorDev,
       A.observacao                   AS observacao
FROM sqldados.nf AS N
       INNER JOIN sqldados.nfAutorizacao AS A
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
       LEFT JOIN sqldados.inv AS I
                 ON N.storeno = I.storeno AND
                    (N.remarks REGEXP CONCAT('NI *', I.invno)
                      OR N.print_remarks REGEXP CONCAT('NI *', I.invno))
       LEFT JOIN sqldados.users AS S
                 ON S.no = A.usernoSing
WHERE (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND (@PESQUISA = '' OR
       CONCAT(N.nfno, '/', N.nfse) LIKE @PESQUISA_START OR
       N.custno LIKE @PESQUISA_INT OR
       C.name LIKE @PESQUISA_LIKE)
