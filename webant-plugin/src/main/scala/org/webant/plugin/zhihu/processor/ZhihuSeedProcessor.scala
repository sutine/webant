package org.webant.plugin.zhihu.processor

import org.apache.log4j.LogManager
import org.webant.plugin.weiboyi.data.WeiboyiDetailData
import org.webant.worker.processor.JsonPageProcessor

import scala.io.Source

class ZhihuSeedProcessor extends JsonPageProcessor[WeiboyiDetailData] {
  private val logger = LogManager.getLogger(classOf[ZhihuSeedProcessor])
  regex = "https://www.zhihu.com/"

  override def links(): Iterable[String] = {
//    val ids = loadIds("D:\\workspace\\webant\\data\\zhihu\\uid_idsn.csv")
    val ids = loadIds("./data/zhihu/uid_idsn.csv")
    if (ids == null || ids.isEmpty)
      return Iterable.empty

    ids.map(id =>
      s"https://www.zhihu.com/api/v4/members/$id/answers?offset=0&limit=20&sort_by=created&include=data[*].is_normal,is_collapsed,collapse_reason,suggest_edit,comment_count,can_comment,content,voteup_count,reshipment_settings,comment_permission,mark_infos,created_time,updated_time,review_info,relationship.is_authorized,voting,is_author,is_thanked,is_nothelp,upvoted_followees;data[*].author.badge[?(type=best_answerer)].topics")
  }

  private def loadIds(path: String): Iterable[String] = {
    try {
      Source.fromFile(path, "UTF-8").getLines().map(line => line.split(",")(1)).toSet
    } catch {
      case e: Exception =>
        logger.error(s"load ids file failed! error: ${e.getMessage}")
        Iterable.empty
    }
  }
}
