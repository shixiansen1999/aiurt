
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>YuntaiControlType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="YuntaiControlType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TurnUp"/>
 *     &lt;enumeration value="TurnDown"/>
 *     &lt;enumeration value="TurnLeft"/>
 *     &lt;enumeration value="TurnRight"/>
 *     &lt;enumeration value="TurnStop"/>
 *     &lt;enumeration value="TurnReset"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "YuntaiControlType")
@XmlEnum
public enum YuntaiControlType {

    @XmlEnumValue("TurnUp")
    TURN_UP("TurnUp"),
    @XmlEnumValue("TurnDown")
    TURN_DOWN("TurnDown"),
    @XmlEnumValue("TurnLeft")
    TURN_LEFT("TurnLeft"),
    @XmlEnumValue("TurnRight")
    TURN_RIGHT("TurnRight"),
    @XmlEnumValue("TurnStop")
    TURN_STOP("TurnStop"),
    @XmlEnumValue("TurnReset")
    TURN_RESET("TurnReset");
    private final String value;

    YuntaiControlType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static YuntaiControlType fromValue(String v) {
        for (YuntaiControlType c: YuntaiControlType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
