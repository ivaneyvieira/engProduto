INSERT IGNORE sqldados.emailDevolucao (chave, fromEmail, toEmail, ccEmail, bccEmail, subject, enviado, htmlContent)
SELECT :chave, :fromEmail, :toEmail, :ccEmail, :bccEmail, :subject, :enviado, :htmlContent
FROM
  dual
WHERE :id = 0;

REPLACE sqldados.emailDevolucao (id, chave, fromEmail, toEmail, ccEmail, bccEmail, subject, enviado, htmlContent)
SELECT :id, :chave, :fromEmail, :toEmail, :ccEmail, :bccEmail, :subject, :enviado, :htmlContent
FROM
  dual
WHERE :id > 0;

SELECT MAX(id)
FROM
  sqldados.emailDevolucao
WHERE id = :id
   OR :id = 0




