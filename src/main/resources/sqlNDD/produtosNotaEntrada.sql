SELECT OID                   AS id,
       COALESCE(XML_NFE, '') AS xmlNfe,
       CASE
	 WHEN PATINDEX('%<nProt>%', XML_AUT) > 0 AND PATINDEX('%</nProt>%', XML_AUT) > 0
	   THEN SUBSTRING(XML_AUT, PATINDEX('%<nProt>%', XML_AUT) + len('<nProt>'),
			  PATINDEX('%</nProt>%', XML_AUT) - PATINDEX('%<nProt>%', XML_AUT) -
			  len('<nProt>'))
	 ELSE ''
       END                   AS numeroProtocolo,
       CASE
	 WHEN PATINDEX('%<dhRecbto>%', XML_AUT) > 0 AND PATINDEX('%</dhRecbto>%', XML_AUT) > 0
	   THEN SUBSTRING(XML_AUT, PATINDEX('%<dhRecbto>%', XML_AUT) + len('<dhRecbto>'),
			  PATINDEX('%</dhRecbto>%', XML_AUT) - PATINDEX('%<dhRecbto>%', XML_AUT) -
			  len('<dhRecbto>'))
	 ELSE ''
       END                   AS dataHoraRecebimento
FROM NDD_COLD.dbo.entrada_nfe
WHERE OID = :id
  AND (XML_NFE IS NOT NULL )
