INSERT IGNORE INTO sqldados.invAgenda (invno)
VALUES (:invno);

UPDATE sqldados.invAgenda
SET data                = :data,
    hora                = TIME_TO_SEC(:hora),
    recebedor           = :recebedor,
    dataRecbedor        = :dataRecbedor,
    horaRecebedor       = TIME_TO_SEC(:horaRecebedor),
    conhecimento        = :conhecimento,
    emissaoConhecimento = :emissaoConhecimento,
    coleta              = :coleta
WHERE invno = :invno;

UPDATE sqldados.inv2
SET c1 = DATE_FORMAT(:data, '%d/%m/%Y'),
    c2 = TIME_FORMAT(:hora, '%H:%i')
WHERE invno = :invno