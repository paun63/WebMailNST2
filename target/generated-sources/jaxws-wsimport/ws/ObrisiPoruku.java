
package ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for obrisiPoruku complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obrisiPoruku">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="poruka" type="{http://ws/}poruka" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obrisiPoruku", propOrder = {
    "poruka"
})
public class ObrisiPoruku {

    protected Poruka poruka;

    /**
     * Gets the value of the poruka property.
     * 
     * @return
     *     possible object is
     *     {@link Poruka }
     *     
     */
    public Poruka getPoruka() {
        return poruka;
    }

    /**
     * Sets the value of the poruka property.
     * 
     * @param value
     *     allowed object is
     *     {@link Poruka }
     *     
     */
    public void setPoruka(Poruka value) {
        this.poruka = value;
    }

}
