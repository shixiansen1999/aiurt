
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RobotGasInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="RobotGasInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="robot-ip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="H2S" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="O2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CH4" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TEMP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HUM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PM25" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PM10" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="O3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SF6" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Desc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotGasInfo", propOrder = {
    "robotIp",
    "h2S",
    "co",
    "o2",
    "ch4",
    "temp",
    "hum",
    "pm25",
    "pm10",
    "o3",
    "sf6",
    "desc"
})
public class RobotGasInfo {

    @XmlElement(name = "robot-ip", required = true)
    protected String robotIp;
    @XmlElement(name = "H2S", required = true)
    protected String h2S;
    @XmlElement(name = "CO", required = true)
    protected String co;
    @XmlElement(name = "O2", required = true)
    protected String o2;
    @XmlElement(name = "CH4", required = true)
    protected String ch4;
    @XmlElement(name = "TEMP", required = true)
    protected String temp;
    @XmlElement(name = "HUM", required = true)
    protected String hum;
    @XmlElement(name = "PM25", required = true)
    protected String pm25;
    @XmlElement(name = "PM10", required = true)
    protected String pm10;
    @XmlElement(name = "O3", required = true)
    protected String o3;
    @XmlElement(name = "SF6", required = true)
    protected String sf6;
    @XmlElement(name = "Desc", required = true)
    protected String desc;

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
     * ��ȡh2S���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getH2S() {
        return h2S;
    }

    /**
     * ����h2S���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setH2S(String value) {
        this.h2S = value;
    }

    /**
     * ��ȡco���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCO() {
        return co;
    }

    /**
     * ����co���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCO(String value) {
        this.co = value;
    }

    /**
     * ��ȡo2���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getO2() {
        return o2;
    }

    /**
     * ����o2���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setO2(String value) {
        this.o2 = value;
    }

    /**
     * ��ȡch4���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCH4() {
        return ch4;
    }

    /**
     * ����ch4���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCH4(String value) {
        this.ch4 = value;
    }

    /**
     * ��ȡtemp���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTEMP() {
        return temp;
    }

    /**
     * ����temp���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTEMP(String value) {
        this.temp = value;
    }

    /**
     * ��ȡhum���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHUM() {
        return hum;
    }

    /**
     * ����hum���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHUM(String value) {
        this.hum = value;
    }

    /**
     * ��ȡpm25���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPM25() {
        return pm25;
    }

    /**
     * ����pm25���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPM25(String value) {
        this.pm25 = value;
    }

    /**
     * ��ȡpm10���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPM10() {
        return pm10;
    }

    /**
     * ����pm10���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPM10(String value) {
        this.pm10 = value;
    }

    /**
     * ��ȡo3���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getO3() {
        return o3;
    }

    /**
     * ����o3���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setO3(String value) {
        this.o3 = value;
    }

    /**
     * ��ȡsf6���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSF6() {
        return sf6;
    }

    /**
     * ����sf6���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSF6(String value) {
        this.sf6 = value;
    }

    /**
     * ��ȡdesc���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDesc() {
        return desc;
    }

    /**
     * ����desc���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDesc(String value) {
        this.desc = value;
    }

}
