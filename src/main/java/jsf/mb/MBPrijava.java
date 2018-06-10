/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.mb;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Scope;
import javax.servlet.http.HttpSession;
import ws.Korisnik;
import ws.Wst;

/**
 *
 * @author P
 */
@Named(value = "mbPrijava")
@javax.enterprise.context.RequestScoped
public class MBPrijava implements Serializable {

    
    Wst ws;
    String korisnik;
    String lozinka;
    Korisnik k1;
    
    String porukaPrijava;
    
    

    
    String odgovorTP;
    String labelaZaPovratakLozinke = "";
    String noviPass;
    String noviPassProvera;    
    String poruka1;
    String poruka2;
    String poruka3;
    
    static String tema = "default"; //izvuci iz k1 odabranu temu

    /**
     * Creates a new instance of MBPrijava
     */
    public MBPrijava() {
        ws = new Wst();
    }

    public String prijaviKorisnikaDepr() {
        porukaPrijava = "";
        if(korisnik.isEmpty() || lozinka.isEmpty())
        {
            porukaPrijava = "Molimo unesite e-mail i lozinku";
            return "index.xhtml";
        }
        Korisnik k = new Korisnik();
        k1 = new Korisnik();
        validacijaUsera();
        k.setKorisnikid(korisnik);
        k.setLozinka(lozinka);
        k1 = ws.getWebServiceTestPort().prijava(k);
        if (k1 == null) {
            porukaPrijava = "Pogresan e-mail ili lozinka";
            return "index.xhtml";
        }
       
        return "mailAgent.xhtml";
    }
    public String prijaviKorisnika() {
        boolean valid = false;
        Korisnik k = new Korisnik();
        validacijaUsera();
        k.setKorisnikid(korisnik);
        k.setLozinka(lozinka);
        k = ws.getWebServiceTestPort().prijava(k);
        
        if (k != null)
        {
           valid = true; 
        }
        
	if (valid) 
        {            
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
            session.setAttribute("id", k.getKorisnikid());
            session.setAttribute("ime", k.getIme());
            session.setAttribute("prezime", k.getPrezime());
            session.setAttribute("tp", k.getTp());
            session.setAttribute("alt", k.getAlt());
            
            return "mailAgent.xhtml";
	} 
        else 
        {
            porukaPrijava = "Pogresan e-mail ili lozinka";
	    return "index.xhtml";		
	}
    }
	
    public String logout() 
    {
	FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
	session.invalidate();
	return "index.xhtml";
    }

    
    public void povratakIzgubljeneLozinke()
    {    
         Korisnik k = new Korisnik();
         int br = validacijaZaPovratakSifre();
         if(br == 0)
         {
             k.setKorisnikid(korisnik);
             k.setTp(odgovorTP);
             k.setLozinka(noviPass);
             
             labelaZaPovratakLozinke = ws.getWebServiceTestPort().povratakSifre(k);
         }
       
         
        
         
    }

    public String registracija() {
        return "registracija.xhtml";
    }
    
    public String redirectLozinka()
    {
        return "zaboravljenaLozinka.xhtml";
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setOdgovorTP(String odgovorTP) {
        this.odgovorTP = odgovorTP;
    }

    public String getOdgovorTP() {
        return odgovorTP;
    }

    public String getLabelaZaPovratakLozinke() {
        return labelaZaPovratakLozinke;
    }

    public void setLabelaZaPovratakLozinke(String labelaZaPovratakLozinke) {
        this.labelaZaPovratakLozinke = labelaZaPovratakLozinke;
    }

    public String getPorukaPrijava() {
        return porukaPrijava;
    }

    public void setPorukaPrijava(String porukaPrijava) {
        this.porukaPrijava = porukaPrijava;
    }

    public String getNoviPass() {
        return noviPass;
    }

    public String getNoviPassProvera() {
        return noviPassProvera;
    }

    public void setNoviPass(String noviPass) {
        this.noviPass = noviPass;
    }

    public void setNoviPassProvera(String noviPassProvera) {
        this.noviPassProvera = noviPassProvera;
    }

    public String getPoruka1() {
        return poruka1;
    }

    public String getPoruka2() {
        return poruka2;
    }

    public String getPoruka3() {
        return poruka3;
    }

    public void setPoruka1(String poruka1) {
        this.poruka1 = poruka1;
    }

    public void setPoruka2(String poruka2) {
        this.poruka2 = poruka2;
    }

    public void setPoruka3(String poruka3) {
        this.poruka3 = poruka3;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }
    
    

    public void validacijaUsera()
    {
        korisnik = korisnik.toLowerCase().trim();
        
        if(korisnik.contains("@"))
        {
            String s = korisnik.substring(0, korisnik.indexOf("@"));
            korisnik = s;
            
            
        }
        
    }
    
    public int validacijaZaPovratakSifre()
    {
        int br = 0;
        poruka1="";
        poruka2="";
        poruka3="";
        
        if(!korisnik.isEmpty())
        {
             validacijaUsera();
        }
        else
        {
             poruka1 = "Morate uneti e-mail";  
             br++;
        }
        
        if(odgovorTP.isEmpty())
        {
             poruka2 = "Morate uneti odgovor na tajno pitanje";
             br++;
        } 

        if(noviPass.isEmpty() || noviPassProvera.isEmpty() || !noviPass.equals(noviPassProvera))
        {
             poruka3 = "Lozinke se ne poklapaju ili nisu unete";
             br++;
        }
        
        if(noviPass.length()<8 || noviPassProvera.length()<8)
        {
            poruka3 = "Lozinke moraju biti sacinjene od najmanje 8 karaktera";
            br++;
        }
        
        return br;
    }
    
}
