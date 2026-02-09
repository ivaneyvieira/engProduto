SELECT id,
       chave,
       IF(dataEmail * 1 = 0, NULL, dataEmail) AS dataEmail,
       fromEmail,
       toEmail,
       ccEmail,
       bccEmail,
       subject,
       enviado,
       htmlContent
FROM
  sqldados.emailDevolucao
where chave = :chave


