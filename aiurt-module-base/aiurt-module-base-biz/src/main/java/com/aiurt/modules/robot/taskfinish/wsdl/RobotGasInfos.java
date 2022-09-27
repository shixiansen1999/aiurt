
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>RobotGasInfos complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="RobotGasInfos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GasContents" type="{http://tempuri.org/taskfinish.xsd}RobotGasInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotGasInfos", propOrder = {
    "gasContents"
})
public class RobotGasInfos {

    @XmlElement(name = "GasContents")
    protected List<RobotGasInfo> gasContents;

    /**
     * Gets the value of the gasContents property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gasContents property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGasContents().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RobotGasInfo }
     *
     *
     */
    public List<RobotGasInfo> getGasContents() {
        if (gasContents == null) {
            gasContents = new ArrayList<RobotGasInfo>();
        }
        return this.gasContents;
    }

}
