
CREATE TABLE `poruka` (
  `porukaid` INT(20) NOT NULL,
  `posiljalac` VARCHAR(30) NOT NULL,
  `primalac` VARCHAR(30) NOT NULL,
  `tema` VARCHAR(50) DEFAULT NULL,
  `sadrzaj` LONGTEXT,
  `kategorija` VARCHAR(10) NOT NULL,
  `pregledana` TINYINT(1) NOT NULL,
  `datum` DATETIME NOT NULL,
  `prilog` VARCHAR(70) DEFAULT NULL,
  PRIMARY KEY (`porukaid`),
  KEY `posiljalac` (`posiljalac`),
  KEY `primalac` (`primalac`),
  CONSTRAINT `poruka_ibfk_1` FOREIGN KEY (`posiljalac`) REFERENCES `korisnik` (`korisnikid`),
  CONSTRAINT `poruka_ibfk_2` FOREIGN KEY (`primalac`) REFERENCES `korisnik` (`korisnikid`)
) ENGINE=INNODB DEFAULT CHARSET=latin1;
