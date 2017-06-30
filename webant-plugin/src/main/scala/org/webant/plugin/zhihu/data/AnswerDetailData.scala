package org.webant.plugin.zhihu.data

import org.webant.commons.elasticsearch.annotation.{Index, Type}
import org.webant.commons.entity.HttpDataEntity

@Index("webant")
@Type("data")
class AnswerDetailData extends HttpDataEntity {
  var relationship: Relationship = _
  var mark_infos: Array[MarkInfos] = _
  var excerpt: String = _
  var can_comment: CanComment = _
  var created_time: Long = _
  var updated_time: Long = _
  var id1: Long = _
  var voteup_count: Integer = _
  var collapse_reason: String = _
  var is_collapsed: Boolean = _
  var author: Author = _
  var url: String = _
  var comment_permission: String = _
  var question: Question = _
  var suggest_edit: SuggestEdit = _
  var content: String = _
  var comment_count: Integer = _
  var extras: String = _
  var reshipment_settings: String = _
  var thanks_count: Integer = _
  var is_copyable: Boolean = _
  var `type`: String = _
  var thumbnail: String = _
  var is_normal: Boolean = _
}

class Relationship {
  var upvoted_followees: Array[String] = _
  var is_author: Boolean = _
  var is_nothelp: Boolean = _
  var is_authorized: Boolean = _
  var voting: Integer = _
  var is_thanked: Boolean = _
}

class MarkInfos {
}

class CanComment {
  var status: Boolean = _
  var reason: String = _
}

class Author {
  var is_followed: Boolean = _
  var avatar_url_template: String = _
  var badge: Array[Badge] = _
  var name: String = _
  var headline: String = _
  var gender: Integer = _
  var user_type: String = _
  var is_advertiser: Boolean = _
  var avatar_url: String = _
  var is_following: Boolean = _
  var is_org: Boolean = _
  var `type`: String = _
  var url: String = _
  var url_token: String = _
  var id: String = _
}

class Badge {
  var `type`: String = _
  var description: String = _
}

class Question {
  var author: Author = _
  var url: String = _
  var created: Long = _
  var updated_time: Long = _
  var question_type: String = _
  var title: String = _
  var `type`: String = _
  var id: String = _
}

class SuggestEdit {
  var status: Boolean = _
  var reason: String = _
  var title: String = _
  var url: String = _
  var unnormal_details: UnnormalDetails = _
  var tip: String = _
}

class UnnormalDetails {
}
