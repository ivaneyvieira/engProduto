USE sqldados;

DO @invno := :invno;
DO @custnoDev := :custnoDev;
DO @saldo := :saldo;
DO @custnoMuda := :custnoMuda;

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = 0
WHERE C.no = @custnoDev
  AND @custnoDev > 0;

UPDATE sqldados.custp AS C
SET C.saldoDevolucao = 0
WHERE C.no = @custnoMuda
  AND @custnoMuda > 0/*;

UPDATE sqldados.nf
SET remarks = TRIM(CONCAT(remarks, ' NI ', :invno, '-', :nfdev, ' ', :tipo))
WHERE storeno = :loja
  AND nfno = :nfno
  AND nfse = :nfse
  AND remarks NOT LIKE CONCAT('%', :tipo, '%')
*/