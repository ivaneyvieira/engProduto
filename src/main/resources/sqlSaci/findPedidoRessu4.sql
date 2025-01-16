DO
@PESQUISA := :pesquisa;
DO
@PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO
@PESQUISALIKE := IF(@PESQUISA REGEXP '[0-9]+', '', CONCAT('%', @PESQUISA, '%'));


DROP
TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE
TEMPORARY TABLE T_PEDIDO
SELECT T.storeno AS loja,
       T.pdvno AS pdvno,
       T.xano AS transacao,
       SO.sname AS lojaOrigem,
       SD.sname AS lojaDestino,
       SO.no AS lojaOrigemNo,
       SD.no AS lojaDestinoNo,
       CAST(CONCAT('Rota', SO.no, SD.no) AS CHAR) AS rota,
       CAST(T.eordno AS CHAR) AS ordno,
       T.custno AS cliente,
       CAST(T.issuedate AS DATE) AS data,
       T.empno AS vendedor,
       U.no AS userno,
       U.name AS usuario,
       CAST(CONCAT(T.nfno, '/', T.nfse) AS CHAR) AS notaTransf,
       T.grossamt / 100 AS valorTransf,
       TRIM(T.remarks) AS observacaoTransf
FROM sqldados.nf AS T
         LEFT JOIN sqldados.store AS SO
                   ON SO.no = T.storeno
         LEFT JOIN sqldados.custp AS C
                   ON C.no = T.custno
         LEFT JOIN sqldados.store AS SD
                   ON SD.cgc = C.cpf_cgc
         LEFT JOIN sqldados.users AS U
                   ON U.no = T.padbits
WHERE T.issuedate >= 20231111
  AND (T.storeno = :storeno OR :storeno = 0)
  AND IFNULL(SD.no, 0) != IFNULL(SO.no, 0)
  AND (T.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (T.issuedate <= :dataFinal OR :dataFinal = 0)
  AND (T.tipo = 1)
  AND (U.name LIKE '%RESSU4%')
GROUP BY T.storeno, T.pdvno, T.xano;


SELECT loja,
       pdvno,
       transacao,
       lojaOrigem,
       lojaDestino,
       rota,
       ordno,
       cliente,
       data,
       vendedor,
       userno,
       usuario,
       notaTransf,
       valorTransf,
       observacaoTransf
FROM T_PEDIDO
WHERE (lojaOrigem = @PESQUISA OR
       lojaDestino = @PESQUISA OR
       cliente = @PESQUISANUM OR
       ordno = @PESQUISANUM OR
       vendedor = @PESQUISANUM OR
       userno = @PESQUISANUM OR
       usuario LIKE @PESQUISALIKE OR
       @PESQUISA = '')