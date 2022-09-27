
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.*;


/**
 * <p>anonymous complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Data" type="{http://tempuri.org/robotdata.xsd}RobotPosInfos"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "data"
})
@XmlRootElement(name = "GetRobotPosInfoResponse")
public class GetRobotPosInfoResponse {

    @XmlElement(name = "Data", required = true)
    protected RobotPosInfos data;

    /**
     * ��ȡdata���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link RobotPosInfos }
     *
     */
    public RobotPosInfos getData() {
        return data;
    }

    /**
     * ����data���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link RobotPosInfos }
     *
     */
    public void setData(RobotPosInfos value) {
        this.data = value;
    }

}
