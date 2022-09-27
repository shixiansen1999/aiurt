
package com.aiurt.modules.robot.taskdata.wsdl;

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
 *         &lt;element name="Data" type="{http://tempuri.org/taskdata.xsd}MapAreaInfos"/>
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
@XmlRootElement(name = "GetMapAreaInfoResponse")
public class GetMapAreaInfoResponse {

    @XmlElement(name = "Data", required = true)
    protected MapAreaInfos data;

    /**
     * ��ȡdata���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link MapAreaInfos }
     *
     */
    public MapAreaInfos getData() {
        return data;
    }

    /**
     * ����data���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link MapAreaInfos }
     *
     */
    public void setData(MapAreaInfos value) {
        this.data = value;
    }

}
