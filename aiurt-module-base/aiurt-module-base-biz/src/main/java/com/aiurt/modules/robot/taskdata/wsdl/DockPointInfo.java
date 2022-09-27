
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>DockPointInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="DockPointInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DockId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DockX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DockY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DockPointInfo", propOrder = {
    "dockId",
    "dockX",
    "dockY",
    "pointList"
})
public class DockPointInfo {

    @XmlElement(name = "DockId", required = true)
    protected String dockId;
    @XmlElement(name = "DockX", required = true)
    protected String dockX;
    @XmlElement(name = "DockY", required = true)
    protected String dockY;
    @XmlElement(name = "PointList")
    protected List<String> pointList;

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

    /**
     * Gets the value of the pointList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pointList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPointList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getPointList() {
        if (pointList == null) {
            pointList = new ArrayList<String>();
        }
        return this.pointList;
    }

}
