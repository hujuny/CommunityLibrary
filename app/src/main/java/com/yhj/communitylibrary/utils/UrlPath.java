package com.yhj.communitylibrary.utils;

/**
 * author : yhj
 * date   : 2019/12/4
 * desc   :存放此App所有url
 */
public class UrlPath {

    /**
     * TODO 易源ISBN扫码api
     * @param isbn
     * @return
     */
//
    public static String path_0(String isbn){
        return "your url";
    }

    /**
     * TODO 易源图片二维码api
     * @return
     */
    public static String path_1(){
        return "your url";
    }

    /**
     * TODO 环信app管理员token获取
     * appname
     * Orgname
     * @return
     */
    public static String path_2(){
        return "https://a1.easemob.com/Orgname/appname/token";
    }

    /**
     * TODO 重置登录密码
     * appname
     * Orgname
     * @param phone
     * @return
     */
    public static String path_3(String phone){
        return "https://a1.easemob.com/Orgname/appname/users/"+phone+"/password";
    }

    /**
     * TODO 在线升级
     * 在tomcat服务器上放置新版本apk，写了一个json文件，解析，通过比较版本号来发现是否有新版本
     * 内容如下
     * {"versionName": "1.1.5", "versionCode": 4, "description": "1.本地图书封面文件上传\n2.在线升级功能添加\n3.图书搜索功能优化", "downloadUrl":"you download apk url"}
     * @return
     */
    public static String path_4(){
        return "your json url";
    }
}
