SELECT X.storeno AS storeno,
       X.pdvno AS pdvno,
       X.xano AS xano,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR) AS notaTransf,
       CAST(CONCAT('Rota', SO.no, SD.no) AS CHAR) AS rota, DATE (N.issuedate) AS data, TRIM (X.prdno) AS codigo, TRIM (MID(P.name, 1, 37)) AS descricao, X.grade AS grade, TRIM (IFNULL(B.barcode, P.barcode)) AS codigoBarras, TRIM (P.mfno_ref) AS referencia, ROUND(X.qtty) AS quant
FROM sqldados.xaprd AS X
    INNER JOIN sqldados.nf AS N
    USING (storeno, pdvno, xano)
    LEFT JOIN sqldados.store AS SO
ON SO.no = N.storeno
    LEFT JOIN sqldados.custp AS C
    ON C.no = N.custno
    LEFT JOIN sqldados.store AS SD
    ON SD.cgc = C.cpf_cgc
    INNER JOIN sqldados.prd AS P
    ON X.prdno = P.no
    LEFT JOIN (SELECT prdno, grade, MAX (barcode) AS barcode
    FROM sqldados.prdbar
    GROUP BY prdno, grade) AS B
    ON B.prdno = X.prdno
    AND B.grade = X.grade
WHERE X.storeno = :loja
  AND X.pdvno = :pdvno
  AND X.xano = :transacao
GROUP BY X.storeno, X.pdvno, X.grade, X.prdno, X.grade
ORDER BY descricao, grade