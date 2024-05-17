USE sqldados;

DROP TABLE IF EXISTS validadeAdicional;
CREATE TABLE validadeAdicional
(
  validade        INT NOT NULL PRIMARY KEY,
  mesesFabricacao INT NOT NULL
);

INSERT INTO validadeAdicional (validade, mesesFabricacao)
VALUES (3, 1),
       (6, 2),
       (8, 1),
       (9, 2),
       (12, 3),
       (15, 3),
       (18, 4),
       (24, 4),
       (30, 6),
       (36, 13),
       (48, 6),
       (60, 12),
       (70, 12),
       (120, 12)
;