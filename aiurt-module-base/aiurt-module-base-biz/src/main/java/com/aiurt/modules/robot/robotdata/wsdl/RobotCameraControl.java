
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
 *         &lt;element name="Type" type="{http://tempuri.org/robotdata.xsd}CameraControlType"/>
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
    "type"
})
@XmlRootElement(name = "RobotCameraControl")
public class RobotCameraControl {

    @XmlElement(name = "Type", required = true)
    @XmlSchemaType(name = "string")
    protected CameraControlType type;

    /**
     * ��ȡtype���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link CameraControlType }
     *
     */
    public CameraControlType getType() {
        return type;
    }

    /**
     * ����type���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link CameraControlType }
     *
     */
    public void setType(CameraControlType value) {
        this.type = value;
    }

}
