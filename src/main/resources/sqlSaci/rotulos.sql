SELECT form_label as rotulo, COUNT(*) AS qt
FROM sqldados.prdalq
GROUP BY form_label
ORDER BY qt DESC
