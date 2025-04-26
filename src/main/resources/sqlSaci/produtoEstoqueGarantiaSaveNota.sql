SELECT numero, numloja, data, hora, usuario, prdno, grade, estoqueSis, estoqueReal, loteDev
FROM
  sqldados.produtoEstoqueGarantia
GROUP BY numloja, numero, prdno, grade;

SELECT invno, prdno, grade, numero, tipoDevolucao, quantDevolucao
FROM
  sqldados.iprdAdicionalDev
GROUP BY invno, prdno, grade, tipoDevolucao, numero;

SELECT *
FROM
  sqldados.invAdicional
GROUP BY invno, tipoDevolucao, numero;

SELECT * FROM sqldados.in
order by invno;

SELECT * FROM sqldados.nf
order by issuedate;



