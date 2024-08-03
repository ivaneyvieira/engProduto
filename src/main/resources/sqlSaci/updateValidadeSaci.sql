UPDATE sqldados.prd
SET tipoGarantia = :tipoValidade,
    garantia     = :tempoValidade
WHERE no = :prdno