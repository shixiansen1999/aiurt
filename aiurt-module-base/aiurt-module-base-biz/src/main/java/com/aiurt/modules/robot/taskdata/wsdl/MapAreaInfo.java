
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>MapAreaInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="MapAreaInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AreaId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AreaName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AreaMap" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MaxX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MaxY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MinX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MinY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ParentId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapAreaInfo", propOrder = {
    "robotIp",
    "areaId",
    "areaName",
    "areaMap",
    "maxX",
    "maxY",
    "minX",
    "minY",
    "parentId"
})
public class MapAreaInfo {

    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "AreaId", required = true)
    protected String areaId;
    @XmlElement(name = "AreaName", required = true)
    protected String areaName;
    @XmlElement(name = "AreaMap", required = true)
    protected String areaMap;
    @XmlElement(name = "MaxX", required = true)
    protected String maxX;
    @XmlElement(name = "MaxY", required = true)
    protected String maxY;
    @XmlElement(name = "MinX", required = true)
    protected String minX;
    @XmlElement(name = "MinY", required = true)
    protected String minY;
    @XmlElement(name = "ParentId", required = true)
    protected String parentId;

    /**
     * ��ȡrobotIp���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRobotIp() {
        return robotIp;
    }

    /**
     * ����robotIp���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRobotIp(String value) {
        this.robotIp = value;
    }

    /**
     * ��ȡareaId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * ����areaId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAreaId(String value) {
        this.areaId = value;
    }

    /**
     * ��ȡareaName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * ����areaName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAreaName(String value) {
        this.areaName = value;
    }

    /**
     * ��ȡareaMap���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAreaMap() {
        return areaMap;
    }

    /**
     * ����areaMap���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAreaMap(String value) {
        this.areaMap = value;
    }

    /**
     * ��ȡmaxX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaxX() {
        return maxX;
    }

    /**
     * ����maxX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxX(String value) {
        this.maxX = value;
    }

    /**
     * ��ȡmaxY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaxY() {
        return maxY;
    }

    /**
     * ����maxY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxY(String value) {
        this.maxY = value;
    }

    /**
     * ��ȡminX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMinX() {
        return minX;
    }

    /**
     * ����minX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMinX(String value) {
        this.minX = value;
    }

    /**
     * ��ȡminY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMinY() {
        return minY;
    }

    /**
     * ����minY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMinY(String value) {
        this.minY = value;
    }

    /**
     * ��ȡparentId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * ����parentId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParentId(String value) {
        this.parentId = value;
    }

}
