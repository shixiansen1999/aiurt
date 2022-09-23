
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>PatrolPointInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="PatrolPointInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PointId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DeviceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AreaId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DockId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PatrolPointInfo", propOrder = {
    "pointId",
    "pointName",
    "pointType",
    "deviceType",
    "areaId",
    "dockId",
    "robotIp"
})
public class PatrolPointInfo {

    @XmlElement(name = "PointId", required = true)
    protected String pointId;
    @XmlElement(name = "PointName", required = true)
    protected String pointName;
    @XmlElement(name = "PointType", required = true)
    protected String pointType;
    @XmlElement(name = "DeviceType", required = true)
    protected String deviceType;
    @XmlElement(name = "AreaId", required = true)
    protected String areaId;
    @XmlElement(name = "DockId", required = true)
    protected String dockId;
    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;

    /**
     * ��ȡpointId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPointId() {
        return pointId;
    }

    /**
     * ����pointId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPointId(String value) {
        this.pointId = value;
    }

    /**
     * ��ȡpointName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPointName() {
        return pointName;
    }

    /**
     * ����pointName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPointName(String value) {
        this.pointName = value;
    }

    /**
     * ��ȡpointType���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPointType() {
        return pointType;
    }

    /**
     * ����pointType���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPointType(String value) {
        this.pointType = value;
    }

    /**
     * ��ȡdeviceType���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * ����deviceType���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDeviceType(String value) {
        this.deviceType = value;
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

}
