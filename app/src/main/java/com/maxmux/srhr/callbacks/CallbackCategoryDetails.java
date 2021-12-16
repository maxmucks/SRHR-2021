package com.maxmux.srhr.callbacks;

import com.maxmux.srhr.models.Category;
import com.maxmux.srhr.models.Video;

import java.util.ArrayList;
import java.util.List;

public class CallbackCategoryDetails {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Category category = null;
    public List<Video> posts = new ArrayList<>();

}
