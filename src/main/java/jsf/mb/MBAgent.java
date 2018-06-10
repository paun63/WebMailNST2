/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.mb;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Named;
import javax.enterprise.context.Dependent;

import javax.faces.application.ViewHandler;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.Korisnik;
import ws.Poruka;
import ws.Wst;

/**
 *
 * @author P
 */
@Named(value = "mbAgent")
@javax.enterprise.context.SessionScoped
public class MBAgent implements Serializable{
    
     Korisnik k1;
    
     Wst ws;
     List<Poruka> lista = new ArrayList();
     List<Korisnik> kontakti = new ArrayList();
     List<Korisnik> pretragaKontakt = new ArrayList();
     List<Poruka> poruke;
     List<Poruka> porukePoStranici;
     String kat = "inbox";
     int br = 0;
     int porukeOd = 0;
     int porukeDo = 19;
     
     String korisnik;
     String from;
     String to;
     String pomocniTo;
     String subject;
     String message;
     String attachment="";
     String ime;
     Boolean uspesnostSlanja = null;
     
     int UkupnoPorukaPoKategoriji = 0;
     
     XMLGregorianCalendar poslendjaPromenaPoruka;
     
     String search = "";
     
     Part file;

     Poruka item;
     String prikazPrimalac;
     String prikazPosiljalac;
     String prikazTema;
     String prikazSadrzaj;
     String prikazPrilog;
     int prikazPorukaID;
     XMLGregorianCalendar prikazDatum;
     boolean render = false;
     
     
     int tajmerZaRefresh = 5000;
     
     String stilTema = "default";
     
     private String greskaPretrage;
     private int vrstaPretrage;
     private String uslovPretrage;
     private String kriterijumPretrage;   
     private String rezultatPretrage;
     
    /**
     * Creates a new instance of MBAgent
     */
    public MBAgent() {
        
        ws = new Wst();
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        
        this.k1 = new Korisnik();
        k1.setKorisnikid(session.getAttribute("id").toString());
        k1.setIme(session.getAttribute("ime").toString());
        k1.setPrezime(session.getAttribute("prezime").toString());      
        k1.setAlt(session.getAttribute("alt").toString());
        k1.setTp(session.getAttribute("tp").toString());
        
        this.korisnik = k1.getKorisnikid();
        this.from = k1.getKorisnikid()+"@webmail.com";
        this.ime = k1.getIme();
        
        uzmiListuPoruka();
        vratiPorukeZaKI();
        neprocitaneZaBedz();
        vratiListuKontakata();
        proveriPoruke();
        
    }
    
    public void save() throws IOException {
             String uploads = "c:\\Users\\Paun\\Desktop\\uploads";
             String filename = file.getSubmittedFileName();
            
           

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, new File(uploads, filename).toPath());
        }
        
        catch (IOException e) {
            // Show faces message?
        }
       
         
    }
    
    
    public void uzmiListuPoruka()
    {
        
       lista = ws.getWebServiceTestPort().vratiListuPoruka(k1);
      
    }
    
    private void vratiListuKontakata() {
        
       kontakti = ws.getWebServiceTestPort().vratiListuKontakata(k1);
      
       
    }
    
    public void vratiPorukeZaKI()
    {
        poruke = new ArrayList<>();
        
        switch(kat)
        {
            case "inbox":  vratiZaInbox(); break;
            case "junk":  vratiZaJunk(); break;
            case "drafts":  vratiZaDrafts(); break;
            case "sent":  vratiZaSent(); break;
            case "deleted":  vratiZaDeleted(); break;
            default: System.out.println("N/A"); break;
        }
       
    }
  
    
    public void download() throws IOException {
       if(prikazPrilog == null)
       {
           return;
       }
       File file = new File(prikazPrilog);
      
    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
        
    response.setHeader("Content-Disposition", "attachment;filename="+vratiImeIzPriloga(prikazPrilog));  
    response.setContentLength((int) file.length());  
    ServletOutputStream out = null;  
    try {  
        FileInputStream input = new FileInputStream(file);  
        byte[] buffer = new byte[1024];  
        out = response.getOutputStream();  
        int i = 0;  
        while ((i = input.read(buffer)) != -1) {  
            out.write(buffer);  
            out.flush();  
        }  
        FacesContext.getCurrentInstance().getResponseComplete();  
    } catch (IOException err) {  
        err.printStackTrace();  
    } finally {  
        try {  
            if (out != null) {  
                out.close();  
            }  
        } catch (IOException err) {  
            err.printStackTrace();  
        }  
    } 
    }
    
    public void posaljiPoruku()
    {
        if(to.contains(";"))
        {
            String[] s = to.split(";");
            for (int i = 0; i < s.length; i++) {
                
                 Korisnik primalac = new Korisnik();
                 primalac.setKorisnikid(s[i]);
                 Poruka p = new Poruka();
                 p.setPosiljalac(k1);
                 p.setPrimalac(primalac);
                 p.setTema(subject.trim());
                 p.setSadrzaj(message);
                 p.setPrilog(attachment);
                 ws.getWebServiceTestPort().slanjePoruke(p);
            }
             osveziPoljaZaNovuPoruku();
             uzmiListuPoruka();
             vratiPorukeZaKI();
        }
        
        else
        {
        
        Korisnik primalac = new Korisnik();
        primalac.setKorisnikid(to);
        Poruka p = new Poruka();
        p.setPosiljalac(k1);
        p.setPrimalac(primalac);
        p.setTema(subject.trim());
        p.setSadrzaj(message);
        p.setPrilog(attachment);
        
        uspesnostSlanja = ws.getWebServiceTestPort().slanjePoruke(p);
            System.out.println(uspesnostSlanja);
        if(uspesnostSlanja)
        {
            osveziPoljaZaNovuPoruku();
            uspesnostSlanja = null;
            uzmiListuPoruka();
            vratiPorukeZaKI();
            
        }
        }
    }
    
    public void sacuvajDraft()
    {
        Korisnik primalac = new Korisnik();
        primalac.setKorisnikid(to.substring(0, to.indexOf("@")));
        Poruka p = new Poruka();
        p.setPosiljalac(k1);
        p.setPrimalac(primalac);
        p.setTema(subject.trim());
        p.setSadrzaj(message);
        p.setPrilog(attachment);
        p.setKategorija("drafts");
        p.setDatum(getXMLGregorianCalendarNow());
        
        ws.getWebServiceTestPort().cuvanjeDrafta(p);
        
        osveziPoljaZaNovuPoruku();
        
        uzmiListuPoruka();
        vratiPorukeZaKI();
        
        
        
        
    }
    
    public void posaljiDraft()
    {
        Poruka p = new Poruka();
        for (Poruka p1 : lista) {
            if(p1.getPorukaid()==(prikazPorukaID))
            {
                p1.setKategorija("sent");
                p = p1;
                
            }
        }
        
        uspesnostSlanja = ws.getWebServiceTestPort().slanjePoruke(p);
            System.out.println("Uspesnost slanja drafta: "+uspesnostSlanja);
        if(uspesnostSlanja)
        {
            obrisiPoruku();
            osveziPoljaZaNovuPoruku();
            uspesnostSlanja = null;
            uzmiListuPoruka();
            vratiPorukeZaKI();
            
        }
    }
    
    public void obrisiPoruku()
    {
        if(prikazPorukaID!=0)
        {
            Poruka p = new Poruka();
            p.setPorukaid(prikazPorukaID);
            ws.getWebServiceTestPort().obrisiPoruku(p);
            render = false;
            osveziTabelu();
        }
        
    }
    
    public void kontaktAutoComplete(AjaxBehaviorEvent event)
    {
        pretragaKontakt = new ArrayList();
        for (Korisnik k : kontakti) {
//            if(!to.contains(";")){
                String s1 = to.toLowerCase();
                if(k.getIme().toLowerCase().contains(s1) || k.getPrezime().toLowerCase().contains(s1) || k.getKorisnikid().toLowerCase().contains(s1))
                {
                    pretragaKontakt.add(k);
                }
//            }
//            else
//            {   
//                if(!to.toLowerCase().substring(to.indexOf(";")+1, to.length()-1).trim().isEmpty()){
//                String s = to.toLowerCase().substring(to.indexOf(";")+1, to.length()-1).trim();
//                
//                    if(k.getIme().toLowerCase().contains(s) || k.getPrezime().toLowerCase().contains(s) || k.getKorisnikid().toLowerCase().contains(s))
//                    {
//                        pretragaKontakt.add(k);
//                    }
//                }
//            }
        }
       
    }
    
    public void porukeZaPrikaz()
    {
        porukePoStranici = new ArrayList();
        int br = 0 + porukeOd;
        
        if(poruke.size()> porukeDo){
            
            while (br <= porukeDo) {   

                porukePoStranici.add(poruke.get(br));
                br++;

            }
        }
        else 
        {
           
           
            int razlika = porukeDo - poruke.size();
            int brojac = 20-razlika-1;
            int pokazivac = porukeOd;
            while(brojac > 0)
            {
                porukePoStranici.add(poruke.get(pokazivac));
                pokazivac++;
                brojac--;
               
            }
            
        }
    }
    public void pomerajF()
    {
        if(!(porukeOd + 20 > poruke.size()))
        {
        porukeDo += 20;
        porukeOd += 20;
        porukeZaPrikaz();
        }
    }
    public void pomerajB()
    {
        if(porukeDo - 20 > 0 && porukeOd - 20 >= 0)
        {
            porukeDo -= 20;
             porukeOd -=  20;
        }
        else
        {
            return;
        }
       
        porukeZaPrikaz();
    }
    
    public void pomerajFK()
    {
       while(porukeDo < poruke.size())
       {
           porukeDo += 20;
       }
       porukeOd = porukeDo - 19;
       porukeZaPrikaz();
    }
    
    public void pomerajBK()
    {
        porukeOd = 0;
        porukeDo = 19;
        porukeZaPrikaz();
    }
    
    public void reset()
    {
        pretragaKontakt = new ArrayList();
    }
    
    public void postaviOdabraniKontakt(String s)
    {
//        if(to.contains(";"))
//        {
//            to = to.substring(0, to.indexOf(";")+1);
//        }
        to = s+"@webmail.com";
    }
    
    public void pretraga()
    {   
        porukePoStranici = new ArrayList();
        for (Poruka p : poruke) {
            if(p.getTema().contains(search) || p.getPosiljalac().getKorisnikid().contains(search)|| p.getPosiljalac().getIme().contains(search)||p.getPosiljalac().getPrezime().contains(search))
            {
                porukePoStranici.add(p);
            }
        }
//        if(porukePoStranici.isEmpty())
//        {
//            Poruka p = new Poruka();
//            p.setTema("Nema rezultata koji odgovaraju pretrazi");
//            
//        }
       
        
    }

    
    public String getFrom() {
        return from;
    }

    public String getTo() {
        reset();
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getKat() {
        return kat;
    }

    public void setKat(String kat) {
        
        this.kat = kat;
        vratiPorukeZaKI();
    }

    public int getBr() {
        return br;
    }

    public void setWs(Wst ws) {
        this.ws = ws;
    }

    public void setLista(List<Poruka> lista) {
        this.lista = lista;
    }

    public void setBr(int br) {
        this.br = br;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public List<Korisnik> getKontakti() {
        return kontakti;
    }

    public void setKontakti(List<Korisnik> kontakti) {
        this.kontakti = kontakti;
    }

    public List<Korisnik> getPretragaKontakt() {
        return pretragaKontakt;
    }

    public void setPretragaKontakt(List<Korisnik> pretragaKontakt) {
        this.pretragaKontakt = pretragaKontakt;
    }

    public int getUkupnoPorukaPoKategoriji() {
        return UkupnoPorukaPoKategoriji;
    }

    public void setUkupnoPorukaPoKategoriji(int UkupnoPorukaPoKategoriji) {
        this.UkupnoPorukaPoKategoriji = UkupnoPorukaPoKategoriji;
    }

    public List<Poruka> getPorukePoStranici() {
        return porukePoStranici;
    }

    public void setPorukePoStranici(List<Poruka> porukePoStranici) {
        this.porukePoStranici = porukePoStranici;
    }

    public void setPorukeOd(int porukeOd) {
        this.porukeOd = porukeOd;
    }

    public void setPorukeDo(int porukeDo) {
        this.porukeDo = porukeDo;
    }

    public int getPorukeOd() {
        return porukeOd;
    }

    public int getPorukeDo() {
        return porukeDo;
    }

    public List<Poruka> getPoruke() {
        return poruke;
    }

    public void setPoruke(List<Poruka> poruke) {
        this.poruke = poruke;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
        pretraga();
    }

    public String getPrikazPrimalac() {
        return prikazPrimalac;
    }

    public String getPrikazPosiljalac() {
        return prikazPosiljalac;
    }

    public String getPrikazTema() {
        return prikazTema;
    }

    public String getPrikazSadrzaj() {
        return prikazSadrzaj;
    }

    public void setPrikazPrimalac(String prikazPrimalac) {
        this.prikazPrimalac = prikazPrimalac;
    }

    public void setPrikazPosiljalac(String prikazPosiljalac) {
        this.prikazPosiljalac = prikazPosiljalac;
    }

    public void setPrikazTema(String prikazTema) {
        this.prikazTema = prikazTema;
    }

    public void setPrikazSadrzaj(String prikazSadrzaj) {
        this.prikazSadrzaj = prikazSadrzaj;
    }

    public String getPrikazPrilog() {
        return prikazPrilog;
    }

    public void setPrikazPrilog(String prikazPrilog) {
        this.prikazPrilog = prikazPrilog;
    }

    public XMLGregorianCalendar getPrikazDatum() {
        return prikazDatum;
    }

    public void setPrikazDatum(XMLGregorianCalendar prikazDatum) {
        this.prikazDatum = prikazDatum;
    }
    
    
    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
        
    }
    
    public void setRenderReply(boolean render) {
        this.render = render;
        this.to = prikazPosiljalac+"@webmail.com";
        this.subject = "RE:"+prikazTema;
        
    }

    public String getStilTema() {
        return stilTema;
    }

    public void setStilTema(String stilTema) {
        this.stilTema = stilTema;
        //MBPrijava.tema = this.stilTema;
        //Sacuvati u bazi korisnikovu zeljenu temu
    }
    
     

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
        if(file!=null){
        attachment = "c:\\Users\\Paun\\Desktop\\uploads\\"+file.getSubmittedFileName();
        }
       
    }

    public Poruka getItem() {
        return item;
    }

    public void setItem(Poruka item) {
        this.item = item;
        System.out.println(item.getPorukaid());
        namestiZaPrikaz(item);
        
    }
    
    public void namestiZaPrikaz(Poruka p)
    {
       
       prikazPosiljalac = p.getPosiljalac().getKorisnikid();
       prikazPrimalac = p.getPrimalac().getKorisnikid();
       prikazTema = p.getTema();
       prikazSadrzaj = p.getSadrzaj();
       prikazPrilog = p.getPrilog();
       prikazDatum = p.getDatum();
       prikazPorukaID = p.getPorukaid();
       
       if(!p.isPregledana())
       {
            Poruka p1 = new Poruka();
            p1.setPorukaid(p.getPorukaid());
            ws.getWebServiceTestPort().azurirajPregledano(p1);
            osveziTabelu();
       }
       neprocitaneZaBedz();
       
       render = true;
    }
    
    public void pripremiPrikaz()
    {
       prikazPosiljalac = "";
       prikazPrimalac = "";
       prikazTema = "";
       prikazSadrzaj = "";
       prikazPrilog = "";
    }
    

    private void vratiZaInbox() {
        
        
        for (Poruka poruka : lista) {
            if(poruka.getPrimalac().getKorisnikid().equals(k1.getKorisnikid()))
            {
                poruke.add(poruka);
               
            }
        }
        UkupnoPorukaPoKategoriji = poruke.size();
         porukeOd = 0;
         porukeDo = 19;
        porukeZaPrikaz();
    }

    private void vratiZaJunk() {
         for (Poruka poruka : lista) {
            if(poruka.getKategorija().equals("junk"))
            {
                poruke.add(poruka);
            }
        }
          UkupnoPorukaPoKategoriji = poruke.size();
          porukeOd = 0;
         porukeDo = 19;
         porukeZaPrikaz();
    }

    private void vratiZaDrafts() {
        for (Poruka poruka : lista) {
            if(poruka.getKategorija().equals("drafts") && poruka.getPosiljalac().getKorisnikid().equals(k1.getKorisnikid()))
            {
                poruke.add(poruka);
            }
        }
         UkupnoPorukaPoKategoriji = poruke.size();
         porukeOd = 0;
         porukeDo = 19;
         porukeZaPrikaz();
    }

    private void vratiZaSent() {
        for (Poruka poruka : lista) {
            if(poruka.getPosiljalac().getKorisnikid().equals(k1.getKorisnikid())&&!poruka.getKategorija().equals("drafts"))
            {
                poruke.add(poruka);
            }
        }
       UkupnoPorukaPoKategoriji = poruke.size();
         porukeOd = 0;
         porukeDo = 19;
       porukeZaPrikaz();
    }

    private void vratiZaDeleted() {
        for (Poruka poruka : lista) {
            if(poruka.getKategorija().equals("deleted"))
            {
                poruke.add(poruka);
            }
        }
         UkupnoPorukaPoKategoriji = poruke.size();
         porukeOd = 0;
         porukeDo = 19;
         porukeZaPrikaz();
    }
    
    public void neprocitaneZaBedz()
    {
        if(kat.equals("inbox"))
        {
            br=0;
        }
        
        
        for (Poruka poruka : poruke) {
            if(!poruka.isPregledana() && poruka.getKategorija().equals("sent") && poruka.getPrimalac().getKorisnikid().equals(korisnik)&&kat.equals("inbox"))
            {
                br++;
            }
        }
         UkupnoPorukaPoKategoriji = poruke.size();
         porukeOd = 0;
         porukeDo = 19;
         porukeZaPrikaz();
    }

    private void osveziPoljaZaNovuPoruku() {
        to = "";
        subject = "";
        message = "";
        attachment = null;
    }
    
    
    public XMLGregorianCalendar getXMLGregorianCalendarNow() 
            
    {
         try {
             GregorianCalendar gregorianCalendar = new GregorianCalendar();
             DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
             XMLGregorianCalendar now =
                     datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
             return now;
         } catch (DatatypeConfigurationException ex) {
             Logger.getLogger(MBAgent.class.getName()).log(Level.SEVERE, null, ex);
             System.out.println("Greska kod generisanja datuma za draft" +ex.getMessage());
             return null;
         }
    }
    
    public String vratiImeIzPriloga(String prilog)
    {
        
        return prilog.replace("c:\\Users\\Paun\\Desktop\\uploads\\", " ");
    }
    
    public void proveriPoruke()
    {
//        new Timer().schedule(new TimerTask() {
//        public void run()  {
//            
//            uzmiListuPoruka();
//            vratiPorukeZaKI();
//         
//        }
//        }, 1, 5000);
    }
    public void osveziTabelu()
    {
        uzmiListuPoruka();
        vratiPorukeZaKI();
        neprocitaneZaBedz();
    }


    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }

    public String elastic() {
        return "elastic.xhtml";
    }
    
     public void kreiranjeIndeksaKorisnika() throws Exception
    {
        int br = 1;
        String json = "";
        
        for (Korisnik k : kontakti)
        {            
            json = "{ \"ime\": \""+k.getIme()+"\", \"prezime\": \""+k.getPrezime()+"\", \"email\": \""+k.getAlt()+"\" }";

            String url = "http://localhost:9200/korisnici/korisnici/"+br;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");         
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();

            if (response.toString().contains("updated"))
            {
                greskaPretrage = "Korisnici azurirani";
            }
            else if (response.toString().contains("created"))
            {
                greskaPretrage = "Korisnici kreirani";
            }

            br++;
        }
    }
    
    public void kreiranjeIndeksaSifarnika() throws Exception
    {
        int br = 1;
        String json = "";
        
        for (Poruka p : lista)
        {            
            json = "{ \"id\": \""+p.getPorukaid()+"\", \"subject\": \""+p.getTema()+"\" }";

            String url = "http://localhost:9200/poruke/poruke/"+br;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");         
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();

            if (response.toString().contains("updated"))
            {
                greskaPretrage = "Poruke azurirane";
            }
            else if (response.toString().contains("created"))
            {
                greskaPretrage = "Poruke kreirane";
            }

            br++;
        }
        
    }
    
    public void pretragaElastic()
    {
        String rezultat = "";
        int brPom = 0;
        try {
            String url = "http://localhost:9200";
            String kriterijumPretrage = "";
            if (vrstaPretrage == 1)
            {
                url = url + "/korisnici/korisnici/_search";
                kriterijumPretrage = "email";
            }
            else if (vrstaPretrage == 2)
            {
                url = url + "/poruke/poruke/_search";
                kriterijumPretrage = "subject";
            }
            String json = "{\n" + "\"query\" : {\n"+"\"match\":{\n"+"\""+kriterijumPretrage+"\":\""+uslovPretrage+"\"\n"+"}\n"+"}\n"+"}"; 
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");         
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();
            
            brPom = response.toString().indexOf("_source") + 9; 
            
            rezultat = response.toString().substring(brPom, response.toString().length()-4);
            rezultatPretrage = rezultat;
            /*if (vrstaPretrage == 1){
            Korisnik k = JSONConverter.getInstance().JSONToKorisnik(rezultat);
            rezultatPretrage = k.toString(); 
            }
            else if (vrstaPretrage == 2){      
            Sifarnik s = JSONConverter.getInstance().JSONToSifarnik(rezultat);
            rezultatPretrage = s.toString();
            }*/
            
        } catch (FileNotFoundException ex) {
            greskaPretrage = "Ne postoji trazeni indeks";
        }
        catch (Exception ex) {
            greskaPretrage = ex.getMessage();
        }
    }
   
    public void obrisiIndeksaKorisnika() 
    {
        try {
            String url = "http://localhost:9200/korisnici";         
            String kriterijumPretrage = "";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json");         
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();

            if (response.toString().contains("true"))
            {
               greskaPretrage = "Uspesno obrasan indeks korisnika"; 
            }
        } 
        catch (FileNotFoundException ex) {
            greskaPretrage = "Ne postoji indeks korisnika";
        } 
        catch (Exception ex) {
            greskaPretrage = ex.getMessage();
        }
    }
    
    public void obrisiIndeksaSifarnika()
    {
        try {
            String url = "http://localhost:9200/poruke";         
            String kriterijumPretrage = "";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json");         
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();

            if (response.toString().contains("true"))
            {
               greskaPretrage = "Uspesno obrasan indeks sifarnika"; 
            }
        }
        catch (FileNotFoundException ex) {
            greskaPretrage = "Ne postoji indeks sifarnika";
        } 
        catch (IOException ex) {
            greskaPretrage = ex.getMessage();
        }
    }

    public String getGreskaPretrage() {
        return greskaPretrage;
    }

    public void setGreskaPretrage(String greskaPretrage) {
        this.greskaPretrage = greskaPretrage;
    }

    public int getVrstaPretrage() {
        return vrstaPretrage;
    }

    public void setVrstaPretrage(int vrstaPretrage) {
        this.vrstaPretrage = vrstaPretrage;
    }

    public String getUslovPretrage() {
        return uslovPretrage;
    }

    public String getKriterijumPretrage() {
        return kriterijumPretrage;
    }

    public void setUslovPretrage(String uslovPretrage) {
        this.uslovPretrage = uslovPretrage;
    }

    public void setKriterijumPretrage(String kriterijumPretrage) {
        this.kriterijumPretrage = kriterijumPretrage;
    }

    public String getRezultatPretrage() {
        return rezultatPretrage;
    }

    public void setRezultatPretrage(String rezultatPretrage) {
        this.rezultatPretrage = rezultatPretrage;
    }
    
}
