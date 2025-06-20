USE sqldados;

SET SQL_MODE = '';

SELECT N.storeno            AS storeno,
       N.pdvno              AS pdvno,
       N.xano               AS xano,
       X.prdno              AS prdno,
       X.grade              AS grade,
       IFNULL(S.ncm, '')    AS ncm,
       N.remarks            AS invnoObs,
       ROUND(X.qtty)        AS qtde,
       X.price / 100        AS valorUnitario,
       N.netamt / 100       AS baseIcms,
       N.icms_amt / 100     AS valorIcms,
       N.ipi_amt / 100      AS valorIpi,
       0.00                 AS icmsAliq,
       0.00                 AS ipiAliq,
       IFNULL(OP.name, '')  AS natureza,
       N.eordno             AS pedido,
       CAST(O.date AS DATE) AS dataPedido
FROM
  sqldados.nf                   AS N
    INNER JOIN sqldados.xaprd   AS X
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.prp     AS I
               ON I.storeno = 10 AND I.prdno = X.prdno
    LEFT JOIN  sqldados.spedprd AS S
               ON X.prdno = S.prdno
    LEFT JOIN  sqldados.natop   AS OP
               ON OP.no = N.natopno
    LEFT JOIN  sqldados.eord    AS O
               ON O.storeno = N.storeno AND O.ordno = N.eordno
WHERE N.storeno = :storeno
  AND N.pdvno = :pdvno
  AND N.xano = :xano
