USE sqldados;

SET SQL_MODE = '';

DO @DATA_FINAL := ROUND(CURDATE() * 1);

DROP TEMPORARY TABLE IF EXISTS T_DADOS_DEV;
CREATE TEMPORARY TABLE T_DADOS_DEV
(
  PRIMARY KEY (invno)
)
SELECT invno
FROM
  sqldados.dadosDev                     AS N
    INNER JOIN sqldados.dadosDevProduto AS P
               USING (invno)
    INNER JOIN sqldados.inv             AS I
               USING (invno)
WHERE P.prdno = :prdno
  AND P.grade = :grade
  AND I.storeno = :loja
  AND I.comp_date BETWEEN :dataInicial AND @DATA_FINAL
  AND IFNULL(N.impressora, '') != '';

DROP TEMPORARY TABLE IF EXISTS T_MOVIMENTACAO_KARDEC;
CREATE TEMPORARY TABLE T_MOVIMENTACAO_KARDEC
(
  tipo varchar(30)
)
SELECT N.invno                        AS ni,
       N.storeno                      AS loja,
       P.prdno                        AS prdno,
       P.grade                        AS grade,
       N.storeno                      AS ljDoc,
       ''                             AS nfEnt,
       CAST(N.comp_date AS date)      AS data,
       CONCAT(N.nfname, '/', N.invse) AS doc,
       N.ordno                        AS pedido,
       'DEVOLUCAO'                    AS tipo,
       DATE(NULL)                     AS vencimento,
       ROUND(P.qtty / 1000)           AS qtde,
       remarks                        AS observacao,
       0                              AS saldo,
       U.login                        AS userLogin,
       UR.login                       AS recLogin,
       UE.login                       AS entLogin
FROM
  sqldados.inv                                AS N
    LEFT JOIN  sqldados.users                 AS U
               ON U.no = IF(N.usernoLast = 0, N.usernoFirst, N.usernoLast)
    INNER JOIN sqldados.iprd                  AS P
               USING (invno)
    LEFT JOIN  sqldados.devClienteAutorizacao AS A
               USING (invno, prdno, grade)
    LEFT JOIN  sqldados.users                    UE
               ON UE.no = A.userEntrega
    LEFT JOIN  sqldados.users                 AS UR
               ON UR.no = A.userRecebimento
WHERE P.prdno = :prdno
  AND P.grade = :grade
  AND N.storeno = :loja
  AND (N.type = 2 OR N.account = '2.01.25')
  AND N.bits & POW(2, 4) = 0
  AND N.invno NOT IN ( SELECT nfNfno FROM sqldados.inv WHERE auxShort13 & POW(2, 15) != 0 )
  AND N.comp_date BETWEEN :dataInicial AND @DATA_FINAL
  AND P.prdno NOT IN ( SELECT prdno FROM sqldados.produtos_dev_loja )
  AND (((SUBSTRING_INDEX(P.c10, '|', 1) = 'P') OR (N.remarks REGEXP 'TROCA +P') OR (N.remarks REGEXP 'EST.+ +P') OR
        (N.remarks REGEXP 'REE.+ +P') OR (N.remarks REGEXP 'MUD.+ +P')) OR
       (N.invno IN ( SELECT invno FROM T_DADOS_DEV )));

SELECT loja,
       prdno,
       grade,
       data,
       ljDoc,
       doc,
       nfEnt,
       tipo,
       '' AS observacao,
       vencimento,
       qtde,
       saldo,
       userLogin,
       recLogin,
       entLogin
FROM T_MOVIMENTACAO_KARDEC
