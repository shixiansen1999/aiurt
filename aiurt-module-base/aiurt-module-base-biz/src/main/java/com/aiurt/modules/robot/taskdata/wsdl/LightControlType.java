
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>LightControlType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="LightControlType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LightOn"/>
 *     &lt;enumeration value="LightOff"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "LightControlType")
@XmlEnum
public enum LightControlType {

    @XmlEnumValue("LightOn")
    LIGHT_ON("LightOn"),
    @XmlEnumValue("LightOff")
    LIGHT_OFF("LightOff");
    private final String value;

    LightControlType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LightControlType fromValue(String v) {
        for (LightControlType c: LightControlType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
