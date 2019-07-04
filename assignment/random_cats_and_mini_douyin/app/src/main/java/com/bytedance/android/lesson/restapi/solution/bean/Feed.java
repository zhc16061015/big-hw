package com.bytedance.android.lesson.restapi.solution.bean;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:18
 */
public class Feed {

    // TODO-C2 (1) Implement your Feed Bean here according to the response json
    String image_url;
    String video_url;
    String student_id;
    String user_name;
    boolean success;
    public void setStudent_id(String student_id){
        this.student_id = student_id;
    }
    public void setUser_name(String user_name){
        this.user_name = user_name;
    }
    public void setImage_url(String image_url){
        this.image_url = image_url;
    }
    public void setVideo_url(String video_url){
        this.video_url = video_url;
    }
    public String urlback(){
        return image_url;
    }
}
