INSERT IGNORE sqldados.emailDevolucao (chave, dataEmail, fromEmail, toEmail, ccEmail, bccEmail, subject, enviado,
                                       htmlContent)
SELECT :chave, :dataEmail, :fromEmail, :toEmail, :ccEmail, :bccEmail, :subject, :enviado, :htmlContent
FROM
  dual
WHERE :id = 0;

REPLACE sqldados.emailDevolucao (id, chave, dataEmail, fromEmail, toEmail, ccEmail, bccEmail, subject, enviado,
                                 htmlContent)
SELECT :id, :chave, :dataEmail, :fromEmail, :toEmail, :ccEmail, :bccEmail, :subject, :enviado, :htmlContent
FROM
  dual
WHERE :id > 0;

SELECT MAX(id) AS id
FROM
  sqldados.emailDevolucao
WHERE id = :id
   OR :id = 0




