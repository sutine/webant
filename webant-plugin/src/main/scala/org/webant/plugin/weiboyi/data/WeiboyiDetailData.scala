package org.webant.plugin.weiboyi.data

import java.sql.Date

import org.webant.commons.entity.HttpDataEntity

import scala.beans.BeanProperty

class WeiboyiDetailData extends HttpDataEntity {

  @BeanProperty
  var weibo_id: String = _

  @BeanProperty
  var weibo_type: Integer = _

  @BeanProperty
  var weibo_name: String = _

  @BeanProperty
  var url: String = _

  @BeanProperty
  var face_url: String = _

  @BeanProperty
  var followers_count: String = _

  @BeanProperty
  var age: Integer = _

  @BeanProperty
  var gender: Integer = _

  @BeanProperty
  var is_online: Integer = _

  @BeanProperty
  var introduction: String = _

  @BeanProperty
  var verification_info: String = _

  @BeanProperty
  var is_exclusive: Integer = _

  @BeanProperty
  var area_id: Long = _

  @BeanProperty
  var area_name: String = _

  @BeanProperty
  var location: String = _

  @BeanProperty
  var profit_rate: Float = _

  @BeanProperty
  var profit_grade: Integer = _

  @BeanProperty
  var is_vip: Integer = _

  @BeanProperty
  var is_bluevip: Integer = _

  @BeanProperty
  var is_daren: Integer = _

  @BeanProperty
  var is_shield: Integer = _

  @BeanProperty
  var is_sensitive: Integer = _

  @BeanProperty
  var flag: Integer = _

  @BeanProperty
  var is_famous: Integer = _

  @BeanProperty
  var is_extend: Integer = _

  @BeanProperty
  var owner_admin_id: Integer = _

  @BeanProperty
  var price_maintain_time: Date = _

  @BeanProperty
  var follower_be_identified: Integer = _

  @BeanProperty
  var follower_be_identified_time: Long = _

  @BeanProperty
  var true_name: String = _

  @BeanProperty
  var active_score: String = _

  @BeanProperty
  var last_active_time: Long = _

  @BeanProperty
  var is_promotion: Boolean = _

  @BeanProperty
  var promotion_comment: String = _

  @BeanProperty
  var industry_type: String = _

  @BeanProperty
  var weixin_followers_latest_identified_time: Long = _

  @BeanProperty
  var only_support_original: Integer = _

  @BeanProperty
  var screen_portrait: String = _

  @BeanProperty
  var screen_shot_followers: String = _

  @BeanProperty
  var screen_shot_qr_code: String = _

  @BeanProperty
  var single_graphic_price: String = _

  @BeanProperty
  var multi_graphic_top_price: String = _

  @BeanProperty
  var multi_graphic_second_price: String = _

  @BeanProperty
  var snbt_exponent: String = _

  @BeanProperty
  var single_graphic_read_num: Integer = _

  @BeanProperty
  var multi_graphic_top_read_num: Integer = _

  @BeanProperty
  var multi_graphic_second_read_num: Integer = _

  @BeanProperty
  var multi_graphic_other_read_num: Integer = _

  @BeanProperty
  var graphic_send_num: Integer = _

  @BeanProperty
  var cooperation_degree: String = _

  @BeanProperty
  var last_thirty_days_send_num: Integer = _

  @BeanProperty
  var graphic_send_num_latest_7d: Integer = _

  @BeanProperty
  var account_id: Long = _

  @BeanProperty
  var screen_shot_followers_last_updated_time: String = _

  @BeanProperty
  var tweet_average_posts_num: Integer = _

  @BeanProperty
  var tweet_average_send_num: Integer = _

  @BeanProperty
  var tweet_average_like_num: Integer = _

  @BeanProperty
  var retweet_average_posts_num: Integer = _

  @BeanProperty
  var retweet_average_like_num: Integer = _

  @BeanProperty
  var retweet_average_send_num: Integer = _

  @BeanProperty
  var pack_info: PackInfo = _

  @BeanProperty
  var is_black: Boolean = _

  @BeanProperty
  var gross_deal_price: DealPrice = _

  @BeanProperty
  var net_deal_price: DealPrice = _

  @BeanProperty
  var quote: DealPrice = _

  @BeanProperty
  var orders_weekly: Integer = _

  @BeanProperty
  var orders_monthly: Integer = _

  @BeanProperty
  var can_origin: Integer = _

  @BeanProperty
  var creations: Array[Creation] = _

  @BeanProperty
  var can_collect: Boolean = _

  @BeanProperty
  var can_black: Boolean = _

  @BeanProperty
  var can_repository_tag: Boolean = _

  @BeanProperty
  var pyq_account_daily_update_images: Array[String] = _

  @BeanProperty
  var has_pyq_daily_images: Integer = _

  @BeanProperty
  var weibo_name_cut: String = _




  class PackInfo() {
    @BeanProperty
    var related_url_a: String = _
  }

  class DealPrice() {
    @BeanProperty
    var price_validity_period: String = _

    @BeanProperty
    var multi_graphic_top_price: Integer = _

    @BeanProperty
    var multi_graphic_second_price: Integer = _

    @BeanProperty
    var single_graphic_price: Integer = _

    @BeanProperty
    var single_graphic_price_avg: Integer = _

    @BeanProperty
    var multi_graphic_top_price_avg: Integer = _

    @BeanProperty
    var multi_graphic_second_price_avg: Integer = _
  }

  class Creation() {
    @BeanProperty
    var pack_id: Integer = _

    @BeanProperty
    var creation_type_id: Integer = _

    @BeanProperty
    var selling_point: String = _

    @BeanProperty
    var referenced_price: String = _

    @BeanProperty
    var urgent: Integer = _

    @BeanProperty
    var provide_outline: Integer = _

    @BeanProperty
    var is_online: Integer = _

    @BeanProperty
    var creation_id: Integer = _

    @BeanProperty
    var cType: String = _
  }
}
