package com.hryj.constant;

/**
 * @author 代廷波
 * @className: StaffEduEnmu
 * @description:
 * @create 2018/9/26 0026-10:48
 **/
public enum StaffEduEnmu {

    //学历
    EDUCATION_01("01","小学"),
    EDUCATION_02("02","初中"),
    EDUCATION_03("03","中专"),
    EDUCATION_04("04","高中"),
    EDUCATION_05("05","大专"),
    EDUCATION_06("06","本科"),
    EDUCATION_07("07","硕士"),
    EDUCATION_08("08","博士");

    StaffEduEnmu(String vale, String name){
        this.vale = vale;
        this.name = name;

    }

    private String vale;
    private String name;

    public String getVale() {
        return vale;
    }

    public String getName() {
        return name;
    }

    public static String getStaffEduVale(String name){
        String key=null;
        for (StaffEduEnmu edu : StaffEduEnmu.values()) {
            if(edu.getName().equals(name)){
                key =edu.getVale();
                break;
            }
        }
        return key;
    }

    public static String getStaffEduName(String vale){
        String key=null;
        for (StaffEduEnmu edu : StaffEduEnmu.values()) {
            if(edu.getVale().equals(vale)){
                key =edu.getName();
                break;
            }
        }
        return key;
    }

}
