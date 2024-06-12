UPDATE sqldados.dadosValidade
SET vencimento  = :vencimento,
    inventario  = :inventario,
    dataEntrada = :dataEntrada
WHERE seq = :seq