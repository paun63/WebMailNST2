
CREATE TABLE `korisnik` (
  `korisnikid` VARCHAR(30) NOT NULL,
  `lozinka` VARCHAR(30) NOT NULL,
  `ime` VARCHAR(20) NOT NULL,
  `prezime` VARCHAR(20) NOT NULL,
  `alt` VARCHAR(30) NOT NULL,
  `tp` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`korisnikid`)
) ENGINE=INNODB DEFAULT CHARSET=latin1;

