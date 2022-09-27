
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>PatrolAreaInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="PatrolAreaInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AreaId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AreaName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ParentId" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "PatrolAreaInfo", propOrder = {
    "areaId",
    "areaName",
    "parentId",
    "robotIp"
})
public class PatrolAreaInfo {

    // 区域id
    @XmlElement(name = "AreaId", required = true)
    protected String areaId;
    // 区域名称
    @XmlElement(name = "AreaName", required = true)
    protected String areaName;
    // 所属父区域id
    @XmlElement(name = "ParentId", required = true)
    protected String parentId;
    // 所属机器人ip
    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;

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
