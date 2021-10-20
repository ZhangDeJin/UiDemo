package com.zdj.phone.bean;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/13
 *     desc : 拨号实体
 * </pre>
 */
public class ObjDial {
    private Character mainText;
    private String secondaryText;

    public ObjDial() {
    }

    public ObjDial(Character mainText, String secondaryText) {
        this.mainText = mainText;
        this.secondaryText = secondaryText;
    }

    public Character getMainText() {
        return mainText;
    }

    public void setMainText(Character mainText) {
        this.mainText = mainText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }
}
