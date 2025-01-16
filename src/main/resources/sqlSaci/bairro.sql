select no, name
FROM sqldados.route AS R;

DROP
TEMPORARY TABLE IF EXISTS T_ROTAS;
CREATE
TEMPORARY TABLE T_ROTAS
(
  PRIMARY KEY (routeno)
)
SELECT R.no AS routeno,
       R.name AS routeName,
       R.areano AS areano,
       A.name AS areaName,
       A.city AS cidade,
       R.name REGEXP '^[0-9A-Z]{2}\\.[0-9A-Z]{2} ' AS prefixo
FROM sqldados.route AS R
         INNER JOIN sqldados.area AS A
                    ON A.no = R.areano
WHERE R.no not in (8000, 9001, 2121, 1001)
ORDER BY prefixo, R.name;

-- Bairro
SELECT A.nei AS bairroEntrega,
       A.city AS cidadeEntrega,
       A.state AS ufEntrega,
       R.routeno AS routeno,
       IF(prefixo = 0, R.routeName, TRIM(MID(R.routeName, LOCATE(' ', R.routeName), 100))) AS bairroRota,
       R.areano AS areano,
       R.areaName AS areaName,
       R.cidade AS cidadeRota,
       IF(R.cidade = 'TIMON', 'Timon', IFNULL(RA1.rota, RA2.rota)) AS rota
FROM sqldados.ctadd AS A
         INNER JOIN T_ROTAS AS R
                    ON A.routeno = R.routeno
         LEFT JOIN sqldados.rotasAdd AS RA1
                   ON RA1.cidade = 'TERESINA'
                       AND
                      RA1.bairro = IF(prefixo = 0, R.routeName, TRIM(MID(R.routeName, LOCATE(' ', R.routeName), 100)))
         LEFT JOIN sqldados.rotasAdd AS RA2
                   ON RA2.cidade = 'TERESINA'
                       AND RA2.bairro = A.nei
WHERE A.nei != ''
  AND A.city IN ('TERESINA', 'TIMON')
GROUP BY A.nei
HAVING COUNT (*) > 10

