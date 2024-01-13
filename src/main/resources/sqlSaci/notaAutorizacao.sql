USE sqldados;

/*
CREATE TABLE sqldados.nfAutorizacao
(
  storeno INT,
  pdvno   INT,
  xano    INT,
  PRIMARY KEY (storeno, pdvno, xano)
)
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
       CASE
         WHEN I.remarks LIKE '%EST%BOLETO%' THEN 'Est Boleto'
         WHEN I.remarks LIKE '%EST%CARTAO%' THEN 'Est Cartao'
         WHEN I.remarks LIKE '%EST%DEP%' THEN 'Est Deposito'
         WHEN I.remarks LIKE '%MUDA%CLI%' THEN 'Muda CLiente'
         WHEN I.remarks LIKE '%MUDA%NOTA%' THEN 'Muda Nota'
         ELSE ''
       END                            AS tipoDev,
       IFNULL(SA.name, S.name)        AS autorizacao,
       I.invno                        AS ni,
       CONCAT(I.nfname, '/', I.invse) AS nfDev,
       CAST(I.issue_date AS DATE)     AS dataDev,
       I.grossamt / 100               AS valorDev
FROM sqldados.nf AS N
       INNER JOIN sqldados.nfAutorizacao AS A
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
       LEFT JOIN sqldados.inv AS I
                 ON N.storeno = I.storeno AND
                    (N.remarks LIKE CONCAT('NI%', I.invno, '%')
                      OR N.print_remarks LIKE CONCAT('NI%', I.invno, '%'))
       LEFT JOIN sqldados.eord AS E
                 ON N.storeno = E.storeno
                   AND N.eordno = E.ordno
       LEFT JOIN sqldados.users AS S
                 ON S.no = E.s10
       LEFT JOIN sqldados.users AS SA
                 ON SA.login = S.login
                   AND (SA.bits1 & 1) = 0
WHERE (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND (@PESQUISA = '' OR
       CONCAT(N.nfno, '/', N.nfse) LIKE @PESQUISA_START OR
       N.custno LIKE @PESQUISA_INT OR
       C.name LIKE @PESQUISA_LIKE)
