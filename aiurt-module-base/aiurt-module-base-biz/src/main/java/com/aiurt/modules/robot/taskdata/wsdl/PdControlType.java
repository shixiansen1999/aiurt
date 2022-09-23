
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>PdControlType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="PdControlType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PdForWard"/>
 *     &lt;enumeration value="PdBackWord"/>
 *     &lt;enumeration value="PdStop"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "PdControlType")
@XmlEnum
public enum PdControlType {

    @XmlEnumValue("PdForWard")
    PD_FOR_WARD("PdForWard"),
    @XmlEnumValue("PdBackWord")
    PD_BACK_WORD("PdBackWord"),
    @XmlEnumValue("PdStop")
    PD_STOP("PdStop");
    private final String value;

    PdControlType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PdControlType fromValue(String v) {
        for (PdControlType c: PdControlType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
