USE sqldados;

SHOW CREATE TABLE invConferencia;

CREATE TABLE `invConferencia`
(
  `invno`      INT(10)     NOT NULL DEFAULT '0',
  `storeno`    SMALLINT(5) NOT NULL DEFAULT '0',
  `nfname`     CHAR(32)    NOT NULL DEFAULT '',
  `invse`      CHAR(4)     NOT NULL DEFAULT '',
  `issue_date` INT(11)     NOT NULL DEFAULT '0',
  `nfekey`     CHAR(60)    NOT NULL DEFAULT '',
  PRIMARY KEY (`invno`),
  UNIQUE KEY `nfekey` (`nfekey`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

SHOW CREATE TABLE iprdConferencia;

CREATE TABLE `iprdConferencia`
(
  `invno` INT(10)     NOT NULL DEFAULT '0',
  `prdno` CHAR(16)    NOT NULL DEFAULT '',
  `grade` CHAR(8)     NOT NULL DEFAULT '',
  `qtty`  INT(10)     NOT NULL DEFAULT '0',
  `fob`   BIGINT(15)  NOT NULL DEFAULT '0',
  `s27`   SMALLINT(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`invno`, `prdno`, `grade`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

SELECT *
FROM
  invConferencia;
SELECT *
FROM
  iprdConferencia;

/******************************************************************************/

ALTER TABLE iprdConferencia
  ADD COLUMN `nfekey` CHAR(60) NOT NULL DEFAULT '';

UPDATE iprdConferencia AS X INNER JOIN invConferencia AS I USING (invno)
SET X.nfekey = I.nfekey
WHERE X.nfekey = '';

DELETE
FROM
  iprdConferencia
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
  CHANGE s27 marca SMALLINT(5) NOT NULL DEFAULT '0';

