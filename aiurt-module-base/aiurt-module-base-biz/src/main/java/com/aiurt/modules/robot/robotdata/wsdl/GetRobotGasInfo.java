
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
 *         &lt;element name="NeedAll" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "needAll"
})
@XmlRootElement(name = "GetRobotGasInfo")
public class GetRobotGasInfo {

    @XmlElement(name = "NeedAll")
    protected boolean needAll;

    /**
     * ��ȡneedAll���Ե�ֵ��
     *
     */
    public boolean isNeedAll() {
        return needAll;
    }

    /**
     * ����needAll���Ե�ֵ��
     *
     */
    public void setNeedAll(boolean value) {
        this.needAll = value;
    }

}
