USE sqldados;

SELECT N.storeno                                                                  AS loja,
       N.pdvno                                                                    AS pdv,
       N.xano                                                                     AS transacao,
       CONCAT(N.nfno, '/', N.nfse)                                                AS nfVenda,
       CAST(N.issuedate AS DATE)                                                  AS dataEmissao,
       N.custno                                                                   AS codCliente,
       C.name                                                                     AS nomeCliente,
       N.grossamt / 100                                                           AS valorVenda,
       ''                                                                         AS tipoDev,
       0                                                                          AS usernoSing,
       ''                                                                         AS autorizacao,
       IFNULL(I1.invno, I2.invno)                                                 AS ni,
       IFNULL(CONCAT(I1.nfname, '/', I1.invse), CONCAT(I2.nfname, '/', I2.invse)) AS nfDev,
       CAST(IFNULL(I1.issue_date, I2.issue_date) AS DATE)                         AS dataDev,
       IFNULL(I1.grossamt, I2.grossamt) / 100                                     AS valorDev,
       U.name                                                                     AS usuarioDev,
       ''                                                                         AS observacao
FROM sqldados.nf AS N
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
       LEFT JOIN sqldados.inv AS I1
                 ON (N.nfno = I1.nfNfno AND N.storeno = I1.nfStoreno AND N.nfse = I1.nfNfse)
       LEFT JOIN sqldados.inv AS I2
                 ON (N.storeno = I2.s1 AND N.pdvno = I2.s2 AND N.xano = I2.l2)
       LEFT JOIN sqldados.users AS U
                 ON U.no = IFNULL(IF(I1.usernoLast = 0, I1.usernoFirst, I1.usernoLast),
                                  IF(I2.usernoLast = 0, I2.usernoFirst, I2.usernoLast))
WHERE N.storeno = :loja
  AND N.nfno = :nfno
  AND N.nfse = :nfse

