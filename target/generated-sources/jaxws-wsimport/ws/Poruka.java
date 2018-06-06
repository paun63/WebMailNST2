
package ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for poruka complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="poruka">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="kategorija" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="porukaid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="posiljalac" type="{http://ws/}korisnik" minOccurs="0"/>
 *         &lt;element name="pregledana" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="prilog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primalac" type="{http://ws/}korisnik" minOccurs="0"/>
 *         &lt;element name="sadrzaj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "poruka", propOrder = {
    "datum",
    "kategorija",
    "porukaid",
    "posiljalac",
    "pregledana",
    "prilog",
    "primalac",
    "sadrzaj",
    "tema"
})
public class Poruka {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datum;
    protected String kategorija;
    protected Integer porukaid;
    protected Korisnik posiljalac;
    protected boolean pregledana;
    protected String prilog;
    protected Korisnik primalac;
    protected String sadrzaj;
    protected String tema;

    /**
     * Gets the value of the datum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatum() {
        return datum;
    }

    /**
     * Sets the value of the datum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatum(XMLGregorianCalendar value) {
        this.datum = value;
    }

    /**
     * Gets the value of the kategorija property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKategorija() {
        return kategorija;
    }

    /**
     * Sets the value of the kategorija property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKategorija(String value) {
        this.kategorija = value;
    }

    /**
     * Gets the value of the porukaid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPorukaid() {
        return porukaid;
    }

    /**
     * Sets the value of the porukaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPorukaid(Integer value) {
        this.porukaid = value;
    }

    /**
     * Gets the value of the posiljalac property.
     * 
     * @return
     *     possible object is
     *     {@link Korisnik }
     *     
     */
    public Korisnik getPosiljalac() {
        return posiljalac;
    }

    /**
     * Sets the value of the posiljalac property.
     * 
     * @param value
     *     allowed object is
     *     {@link Korisnik }
     *     
     */
    public void setPosiljalac(Korisnik value) {
        this.posiljalac = value;
    }

    /**
     * Gets the value of the pregledana property.
     * 
     */
    public boolean isPregledana() {
        return pregledana;
    }

    /**
     * Sets the value of the pregledana property.
     * 
     */
    public void setPregledana(boolean value) {
        this.pregledana = value;
    }

    /**
     * Gets the value of the prilog property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrilog() {
        return prilog;
    }

    /**
     * Sets the value of the prilog property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrilog(String value) {
        this.prilog = value;
    }

    /**
     * Gets the value of the primalac property.
     * 
     * @return
     *     possible object is
     *     {@link Korisnik }
     *     
     */
    public Korisnik getPrimalac() {
        return primalac;
    }

    /**
     * Sets the value of the primalac property.
     * 
     * @param value
     *     allowed object is
     *     {@link Korisnik }
     *     
     */
    public void setPrimalac(Korisnik value) {
        this.primalac = value;
    }

    /**
     * Gets the value of the sadrzaj property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSadrzaj() {
        return sadrzaj;
    }

    /**
     * Sets the value of the sadrzaj property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSadrzaj(String value) {
        this.sadrzaj = value;
    }

    /**
     * Gets the value of the tema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTema() {
        return tema;
    }

    /**
     * Sets the value of the tema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTema(String value) {
        this.tema = value;
    }

}
