package org.webant.plugin.toutiao.data;

public class FeedData {
    public String chinese_tag;

    public String media_avatar_url;

    public Boolean is_feed_ad;

    public String tag_url;

    public UgcData ugc_data;

    public String title;

    public Boolean single_mode;

    public Boolean middle_mode;

    public String abstract1;

    public Integer group_source;

    public ImageItem[] image_list;

    public Long behot_time;

    public String source_url;

    public String source;

    public Boolean more_mode;

    public String article_genre;

    public Integer comments_count;

    public Boolean has_gallery;

    public String tag;

    public String image_url;

    public String group_id;

    public String media_url;

    public Boolean has_video;

    public String video_duration_str;

    public String video_id;

    public Integer video_play_count;

    class ImageItem {
        public String url;
    }

    class UgcData {
        public Integer read_count;
        public String[] ugc_images;
        public UgcUser ugc_user;
        public Integer digg_count;
        public String content;
        public Integer comment_count;
    }

    class UgcUser {
        public String open_url;
        public Long user_id;
        public String name;
        public String avatar_url;
        public Boolean is_following;
        public Boolean is_self;
        public Integer user_verified;
        public UserAuthInfo user_auth_info;
    }

    class UserAuthInfo {
        public String auth_type;
        public String auth_info;
    }
}
