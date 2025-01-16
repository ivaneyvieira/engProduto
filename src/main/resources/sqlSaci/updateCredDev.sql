SELECT no AS storeno, name, saldoDevolucao
FROM sqldados.custp
WHERE no IN (200, 300, 400, 500, 600, 700, 800);


SELECT S.no             AS storeno,
       S.no * 100       AS custnoDev,
       C.no             AS custnoLoj,
       C.name,
       C.saldoDevolucao AS saldoDevolucaoLoja,
       D.saldoDevolucao AS saldoDevolucaoDev
FROM sqldados.custp AS C
         INNER JOIN sqldados.store AS S
                    ON C.cpf_cgc = S.cgc
                        AND S.no IN (2, 3, 4, 5, 7, 6, 8)
         INNER JOIN sqldados.custp AS D
                    ON D.no = S.no * 100
WHERE C.cpf_cgc LIKE '07.483.654/%';

DROP TABLE IF EXISTS sqldados.saldoDevolucao;
CREATE TABLE sqldados.saldoDevolucao
(
    invno     INT,
    custnoLoj INT,
    custnoDev INT,
    saldo     BIGINT,
    PRIMARY KEY (invno)
);





