DROP TABLE IF EXISTS sqldados.produtos_dev_loja;
CREATE TABLE sqldados.produtos_dev_loja
(
  PRIMARY KEY (prdno)
)
SELECT no AS prdno, TRIM(no) AS codigo, name AS descricao
FROM sqldados.prd
WHERE no IN (124512, 124513, 124514, 124515, 124516, 124517,
             124518, 124519, 124520, 124521, 124522, 124523,
             124524, 124525, 104509, 109788, 109789, 109790,
             109791, 109792, 109793, 109794, 109795, 109796,
             109797, 109798);


DROP TABLE IF EXISTS sqldados.produtos_uso_consumo;
CREATE TABLE sqldados.produtos_uso_consumo
(
  PRIMARY KEY (prdno)
)
SELECT no AS prdno, TRIM(no) AS codigo, name AS descricao
FROM sqldados.prd
WHERE no IN (980082, 980083, 980084, 980085);