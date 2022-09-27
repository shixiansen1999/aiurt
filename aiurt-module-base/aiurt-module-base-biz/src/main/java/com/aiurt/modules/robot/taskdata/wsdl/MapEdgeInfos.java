
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>MapEdgeInfos complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="MapEdgeInfos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Infos" type="{http://tempuri.org/taskdata.xsd}MapEdgeInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapEdgeInfos", propOrder = {
    "infos"
})
public class MapEdgeInfos {

    @XmlElement(name = "Infos")
    protected List<MapEdgeInfo> infos;

    /**
     * Gets the value of the infos property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the infos property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInfos().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MapEdgeInfo }
     *
     *
     */
    public List<MapEdgeInfo> getInfos() {
        if (infos == null) {
            infos = new ArrayList<MapEdgeInfo>();
        }
        return this.infos;
    }

}
