SELECT form_label AS rotulo, COUNT(*) AS qt
FROM sqldados.prdalq
GROUP BY form_label
ORDER BY qt DESC
