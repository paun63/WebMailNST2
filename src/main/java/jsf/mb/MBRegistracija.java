/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.mb;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import ws.Korisnik;
import ws.Wst;

/**
 *
 * @author P
 */
@Named(value = "mbRegistracija")
@SessionScoped
public class MBRegistracija implements Serializable {

    Wst ws;

    String user;
    String pass;
    String passCheck;
    String ime;
    String prezime;
    String alt;
    String tp;

    Korisnik k;

    String porukaUser;
    String porukaPass;
    String porukaImePrezime;
    String PorukaAlt;
    String porukaTP;

    List<String> korisnici;
    int br = 0;
    boolean uspesnaRegistracija;

    /**
     * Creates a new instance of MBRegistracija
     */
    public MBRegistracija() {

        ws = new Wst();
        vratiKorisnikeZaProveru();

    }

    public void vratiKorisnikeZaProveru() {

        korisnici = ws.getWebServiceTestPort().vratiListuKorisnikID();

    }

    public void registracija() {
        validacija();
        if (br == 0) {
            
            uspesnaRegistracija = ws.getWebServiceTestPort().registracija(k);
 
        }
        
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getAlt() {
        return alt;
    }

    public String getTp() {
        return tp;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getPassCheck() {
        return passCheck;
    }

    public void setPassCheck(String passCheck) {
        this.passCheck = passCheck;
    }

    public List<String> getKorisnici() {
        return korisnici;
    }

    public void setKorisnici(List<String> korisnici) {
        this.korisnici = korisnici;
    }

    public String getPorukaUser() {
        return porukaUser;
    }

    public String getPorukaPass() {
        return porukaPass;
    }

    public String getPorukaImePrezime() {
        return porukaImePrezime;
    }

    public String getPorukaAlt() {
        return PorukaAlt;
    }

    public String getPorukaTP() {
        return porukaTP;
    }

    public boolean isUspesnaRegistracija() {
        return uspesnaRegistracija;
    }

    public void setPorukaUser(String porukaUser) {
        this.porukaUser = porukaUser;
    }

    public void setPorukaPass(String porukaPass) {
        this.porukaPass = porukaPass;
    }

    public void setPorukaImePrezime(String porukaImePrezime) {
        this.porukaImePrezime = porukaImePrezime;
    }

    public void setPorukaAlt(String PorukaAlt) {
        this.PorukaAlt = PorukaAlt;
    }

    public void setPorukaTP(String porukaTP) {
        this.porukaTP = porukaTP;
    }

    public void setUspesnaRegistracija(boolean uspesnaRegistracija) {
        this.uspesnaRegistracija = uspesnaRegistracija;
    }

    private void validacija() {

         porukaUser = "";
         porukaPass = "";
         porukaImePrezime = "";
         PorukaAlt = "";
         porukaTP = "";
         
        k = new Korisnik();
      
        int brUser = 0;
        br = 0;
        
        
        if (user.isEmpty()) {
            porukaUser = "* Korisnicko ime mora biti uneto";
            br++;
        }
        else
        {
            user = user.toLowerCase().trim();
               if(user.contains("@"))
               {
                    String s = user.substring(0, user.indexOf("@"));
                    user = s;
               }
        }

        for (String string : korisnici) {
            if (user.equals(string)) {
                brUser++;
            }

        }

        if (brUser == 0) {
            k.setKorisnikid(user);
        } else {
            porukaUser = "* Uneto korisnicko ime vec postoji";
            br++;
        }

        if (pass.equals(passCheck) && !pass.isEmpty()) {
            if (pass.length() > 8) {
                k.setLozinka(pass);
            } else {
                porukaPass = "* Lozinka mora biti sacinjena od najmanje 8 karaktera";
                br++;
            }
        } else {
            porukaPass = "* Lozinke se ne poklapaju ili nisu unete";
            br++;
        }

        if (!ime.isEmpty()) {
            k.setIme(ime);
        } else {
            porukaImePrezime = "* Ime i prezime moraju biti uneti";
            br++;
        }

        if (!prezime.isEmpty()) {
            k.setPrezime(prezime);
        } else {
            porukaImePrezime = "* Ime i prezime moraju biti uneti";
           br++;
        }

        if (!alt.isEmpty()) {
            k.setAlt(alt);
        } else {
            PorukaAlt = "* Alternativna e-mail adresa mora biti uneta";
            br++;
        }

        if (!tp.isEmpty()) {
            k.setTp(tp);
        } else {
            porukaTP = "* Odgovor na tajno pitanje mora biti unet";
            br++;
        }

       
    }

}
