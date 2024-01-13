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

SELECT N.storeno                   AS loja,
       N.pdvno                     AS pdv,
       N.xano                      AS transacao,
       CONCAT(N.nfno, '/', N.nfse) AS nfVenda,
       CAST(N.issuedate AS DATE)   AS dataEmissao,
       N.custno                    AS codCliente,
       C.name                      AS nomeCliente,
       N.grossamt / 100            AS valorVenda
FROM sqldados.nf AS N
       INNER JOIN sqldados.nfAutorizacao AS A
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
WHERE (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND (@PESQUISA = '' OR
       CONCAT(N.nfno, '/', N.nfse) LIKE @PESQUISA_START OR
       N.custno LIKE @PESQUISA_INT OR
       C.name LIKE @PESQUISA_LIKE)
