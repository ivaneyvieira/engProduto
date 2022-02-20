USE sqldados;

SHOW CREATE TABLE invConferencia;

CREATE TABLE `invConferencia` (
  `invno`      int(10)     NOT NULL DEFAULT '0',
  `storeno`    smallint(5) NOT NULL DEFAULT '0',
  `nfname`     char(32)    NOT NULL DEFAULT '',
  `invse`      char(4)     NOT NULL DEFAULT '',
  `issue_date` int(11)     NOT NULL DEFAULT '0',
  `nfekey`     char(60)    NOT NULL DEFAULT '',
  PRIMARY KEY (`invno`),
  UNIQUE KEY `nfekey`(`nfekey`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

SHOW CREATE TABLE iprdConferencia;

CREATE TABLE `iprdConferencia` (
  `invno` int(10)     NOT NULL DEFAULT '0',
  `prdno` char(16)    NOT NULL DEFAULT '',
  `grade` char(8)     NOT NULL DEFAULT '',
  `qtty`  int(10)     NOT NULL DEFAULT '0',
  `fob`   bigint(15)  NOT NULL DEFAULT '0',
  `s27`   smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`invno`, `prdno`, `grade`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

SELECT *
FROM invConferencia;
SELECT *
FROM iprdConferencia;

/******************************************************************************/

ALTER TABLE iprdConferencia
  ADD COLUMN `nfekey` char(60) NOT NULL DEFAULT '';

UPDATE iprdConferencia AS X INNER JOIN invConferencia AS I USING (invno)
SET X.nfekey = I.nfekey
WHERE X.nfekey = '';

DELETE
FROM iprdConferencia
WHERE nfekey = '';

ALTER TABLE iprdConferencia
  DROP PRIMARY KEY;
ALTER TABLE iprdConferencia
  ADD PRIMARY KEY (nfekey, prdno, grade);

ALTER TABLE invConferencia
  DROP COLUMN invno;
ALTER TABLE invConferencia
  DROP COLUMN storeno;
ALTER TABLE invConferencia
  DROP COLUMN nfname;
ALTER TABLE invConferencia
  DROP COLUMN invse;

ALTER TABLE iprdConferencia
  DROP COLUMN invno;
ALTER TABLE iprdConferencia
  DROP COLUMN fob;

ALTER TABLE iprdConferencia
  CHANGE s27 marca smallint(5) NOT NULL DEFAULT '0';

