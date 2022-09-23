
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>PatrolDockPathInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="PatrolDockPathInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DockId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DockX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DockY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PatrolDockPathInfo", propOrder = {
    "dockId",
    "dockX",
    "dockY"
})
public class PatrolDockPathInfo {

    @XmlElement(name = "DockId", required = true)
    protected String dockId;
    @XmlElement(name = "DockX", required = true)
    protected String dockX;
    @XmlElement(name = "DockY", required = true)
    protected String dockY;

    /**
     * ��ȡdockId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDockId() {
        return dockId;
    }

    /**
     * ����dockId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDockId(String value) {
        this.dockId = value;
    }

    /**
     * ��ȡdockX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDockX() {
        return dockX;
    }

    /**
     * ����dockX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDockX(String value) {
        this.dockX = value;
    }

    /**
     * ��ȡdockY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDockY() {
        return dockY;
    }

    /**
     * ����dockY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDockY(String value) {
        this.dockY = value;
    }

}
