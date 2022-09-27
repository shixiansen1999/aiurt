
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
 *         &lt;element name="LinearVelocity" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="AngularVelocity" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
    "linearVelocity",
    "angularVelocity"
})
@XmlRootElement(name = "RobotSpeedControl")
public class RobotSpeedControl {

    @XmlElement(name = "LinearVelocity")
    protected double linearVelocity;
    @XmlElement(name = "AngularVelocity")
    protected double angularVelocity;

    /**
     * ��ȡlinearVelocity���Ե�ֵ��
     *
     */
    public double getLinearVelocity() {
        return linearVelocity;
    }

    /**
     * ����linearVelocity���Ե�ֵ��
     *
     */
    public void setLinearVelocity(double value) {
        this.linearVelocity = value;
    }

    /**
     * ��ȡangularVelocity���Ե�ֵ��
     *
     */
    public double getAngularVelocity() {
        return angularVelocity;
    }

    /**
     * ����angularVelocity���Ե�ֵ��
     *
     */
    public void setAngularVelocity(double value) {
        this.angularVelocity = value;
    }

}
