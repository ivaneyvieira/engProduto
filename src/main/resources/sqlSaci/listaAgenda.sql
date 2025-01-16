DO @LOJA := :loja;
DO @FILTRO_LIKE := CONCAT(:filtro, '%');
DO @FILTRO_NUM := IF(:filtro REGEXP '^[0-9]+', :filtro * 1, NULL);
DO @FILTRO_STR := IF(:filtro REGEXP '^[0-9]+', NULL, :filtro);
DO @FILTRO_COD := :filtro;

DROP TABLE IF EXISTS sqldados.T_INV2;
CREATE TEMPORARY TABLE sqldados.T_INV2 /*T2*/
SELECT inv2.storeno                                               AS loja,
       IF(inv2.c1 <> '',
          TRUNCATE(CONCAT(RIGHT(inv2.c1, 4), MID(inv2.c1, 4, 2), LEFT(inv2.c1, 2)), 0) * 1,
          IF(vend.state = 'PI', DATE_ADD(inv2.issue_date, INTERVAL 2 DAY) * 1,
             DATE_ADD(inv2.issue_date, INTERVAL 1 MONTH)) * 1)    AS data,
       LEFT(inv2.c2, 8)                                           AS hora,
       IFNULL(emp.no, 0)                                          AS empno,
       IFNULL(emp.sname, '')                                      AS recebedor,
       IF(IFNULL(emp.sname, '') = '', NULL,
          TRUNCATE(CONCAT(RIGHT(inv2.c3, 4), MID(inv2.c3, 4, 2), LEFT(inv2.c3, 2)), 0) *
          1)                                                      AS dataRecbedor,
       IF(IFNULL(emp.sname, '') = '', NULL,
          LEFT(inv2.c4, 8))                                       AS horaRecebedor,
       IFNULL(inv2.c5, '')                                        AS conhecimento,
       inv2.invno                                                 AS invno,
       inv2.vendno                                                AS forn,
       IFNULL(vend.sname, 'NAO ENCONTRADO')                       AS abrev,
       IFNULL(carr.cgc, '')                                       AS cnpj,
       IF(inv2.nfname = 0, CAST(inv2.invno AS CHAR), inv2.nfname) AS nf,
       inv2.issue_date                                            AS emissao,
       inv2.ordno                                                 AS pedido,
       CAST(inv2.l2 AS CHAR)                                      AS conh,
       inv2.carrno                                                AS transp,
       CAST(IFNULL(LEFT(carr.name, 10), 'NAO ENCONT') AS CHAR)    AS nome,
       CAST(inv2.packages AS CHAR)                                AS volume,
       inv2.grossamt                                              AS total,
       IF(TRIM(inv2.c1) <> '' AND
          (TRIM(LEFT(inv2.c2, 5)) <> '' AND TRIM(LEFT(inv2.c2, 5)) <> '00:00'), 'S',
          'N')                                                    AS agendado,
       IF(emp.sname IS NULL, 'N', 'S')                            AS recebido,
       IF((ords.bits & POW(2, 3)) != 0, 'FOB', 'CIF')             AS frete,
       CAST(IF(inv2.c6 = '', NULL, inv2.c6 * 1) AS DATE)          AS coleta
FROM sqldados.inv2
         LEFT JOIN sqldados.vend
                   ON (vend.no = inv2.vendno)
         LEFT JOIN sqldados.carr AS carr
                   ON (carr.no = inv2.carrno)
         LEFT JOIN sqldados.inv AS inv
                   ON (inv.vendno = inv2.vendno AND inv.nfname = inv2.nfname AND
                       inv.ordno = inv2.ordno AND inv.grossamt = inv2.grossamt)
         LEFT JOIN sqldados.emp
                   ON (emp.no = inv2.auxStr6 AND emp.no <> 0)
         LEFT JOIN sqldados.ords
                   ON (inv2.storeno = ords.storeno AND inv2.ordno = ords.no)
WHERE inv.invno IS NULL
  AND inv2.storeno > 0
  AND (inv2.storeno = @LOJA OR @LOJA = 0);

SELECT loja,
       CAST(IF(data = 0, NULL, data * 1) AS DATE)                 AS data,
       IFNULL(hora, '')                                           AS hora,
       CAST(empno AS UNSIGNED)                                    AS empno,
       IFNULL(recebedor, '')                                      AS recebedor,
       IFNULL(conhecimento, '')                                   AS conhecimento,
       CAST(IF(dataRecbedor = 0, NULL, dataRecbedor * 1) AS DATE) AS dataRecbedor,
       IFNULL(horaRecebedor, '')                                  AS horaRecebedor,
       IFNULL(CAST(invno AS CHAR), '')                            AS invno,
       forn                                                       AS fornecedor,
       abrev                                                      AS abreviacao,
       cnpj                                                       AS cnpj,
       CAST(IF(emissao = 0, NULL, emissao) AS DATE)               AS emissao,
       IFNULL(nf, '')                                             AS nf,
       IFNULL(volume, '')                                         AS volume,
       IFNULL(total / 100, 0.00)                                  AS total,
       transp                                                     AS transp,
       nome                                                       AS nome,
       pedido                                                     AS pedido,
       frete                                                      AS frete,
       coleta                                                     AS coleta
FROM sqldados.T_INV2
WHERE loja <> 0
HAVING (:filtro = '' OR DATE_FORMAT(data, '%d/%m/%Y') LIKE @FILTRO_LIKE OR
        DATE_FORMAT(coleta, '%d/%m/%Y') LIKE @FILTRO_LIKE OR
        DATE_FORMAT(dataRecbedor, '%d/%m/%Y') LIKE @FILTRO_LIKE OR
        DATE_FORMAT(emissao, '%d/%m/%Y') LIKE @FILTRO_LIKE OR recebedor LIKE @FILTRO_LIKE OR
        conhecimento LIKE @FILTRO_LIKE OR frete = @FILTRO_STR OR abreviacao LIKE @FILTRO_LIKE OR
        fornecedor LIKE @FILTRO_STR OR nf LIKE @FILTRO_COD OR volume LIKE @FILTRO_STR OR
        pedido = @FILTRO_NUM OR nome LIKE @FILTRO_LIKE OR empno = @FILTRO_NUM OR
        invno = @FILTRO_NUM OR transp = @FILTRO_NUM)

