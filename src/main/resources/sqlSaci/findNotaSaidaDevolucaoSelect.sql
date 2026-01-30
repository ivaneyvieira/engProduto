USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_NF;
CREATE TEMPORARY TABLE T_NF
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT storeno,
       pdvno,
       xano,
       COALESCE(
           IF(LOCATE(' PED ', CONCAT(N.print_remarks, ' ', N.remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(N.print_remarks, ' ', N.remarks, ' '),
                                        LOCATE(' PED ', CONCAT(N.print_remarks, ' ', N.remarks, ' ')) + 5,
                                        200),
                              ' ', 1), NULL),
           IF(LOCATE(' NID ', CONCAT(N.print_remarks, ' ', N.remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(N.print_remarks, ' ', N.remarks, ' '),
                                        LOCATE(' NID ', CONCAT(N.print_remarks, ' ', N.remarks, ' ')) + 5,
                                        200),
                              ' ', 1), NULL),
           IF(LOCATE(' NI DEV ', CONCAT(N.print_remarks, ' ', N.remarks, ' ')) > 0,
              SUBSTRING_INDEX(SUBSTRING(CONCAT(N.print_remarks, ' ', N.remarks, ' '),
                                        LOCATE(' NI DEV ', CONCAT(N.print_remarks, ' ', N.remarks, ' ')) + 8,
                                        200),
                              ' ', 1), NULL)
       ) * 1 AS niDev
FROM
  sqldados.nf AS N
WHERE storeno = :storeno
  AND pdvno = :pdvno
  AND xano = :xano;

SELECT seq, storeno AS loja, pdvno, xano, CAST(IF(date = 0, NULL, date) AS date) AS date, filename, file
FROM
  T_NF                                         AS N
    INNER JOIN sqldados.invAdicionalDevArquivo AS A
               ON A.numero = N.niDev
    INNER JOIN sqldados.invAdicional           AS IA
               USING (invno, tipoDevolucao, numero)
WHERE IA.situacaoDev = :situacaoDev

