
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WiperControlType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="WiperControlType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="WiperOn"/>
 *     &lt;enumeration value="WiperOff"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "WiperControlType")
@XmlEnum
public enum WiperControlType {

    @XmlEnumValue("WiperOn")
    WIPER_ON("WiperOn"),
    @XmlEnumValue("WiperOff")
    WIPER_OFF("WiperOff");
    private final String value;

    WiperControlType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WiperControlType fromValue(String v) {
        for (WiperControlType c: WiperControlType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
