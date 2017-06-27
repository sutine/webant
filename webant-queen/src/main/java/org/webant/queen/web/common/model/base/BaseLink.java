package org.webant.queen.web.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseLink<M extends BaseLink<M>> extends Model<M> implements IBean {

	public M setId(java.lang.String id) {
		set("id", id);
		return (M)this;
	}

	public java.lang.String getId() {
		return get("id");
	}

	public M setTaskId(java.lang.String taskId) {
		set("taskId", taskId);
		return (M)this;
	}

	public java.lang.String getTaskId() {
		return get("taskId");
	}

	public M setSiteId(java.lang.String siteId) {
		set("siteId", siteId);
		return (M)this;
	}

	public java.lang.String getSiteId() {
		return get("siteId");
	}

	public M setUrl(java.lang.String url) {
		set("url", url);
		return (M)this;
	}

	public java.lang.String getUrl() {
		return get("url");
	}

	public M setReferer(java.lang.String referer) {
		set("referer", referer);
		return (M)this;
	}

	public java.lang.String getReferer() {
		return get("referer");
	}

	public M setPriority(java.lang.Integer priority) {
		set("priority", priority);
		return (M)this;
	}

	public java.lang.Integer getPriority() {
		return get("priority");
	}

	public M setLastCrawlTime(java.util.Date lastCrawlTime) {
		set("lastCrawlTime", lastCrawlTime);
		return (M)this;
	}

	public java.util.Date getLastCrawlTime() {
		return get("lastCrawlTime");
	}

	public M setStatus(java.lang.String status) {
		set("status", status);
		return (M)this;
	}

	public java.lang.String getStatus() {
		return get("status");
	}

	public M setDataVersion(java.lang.String dataVersion) {
		set("dataVersion", dataVersion);
		return (M)this;
	}

	public java.lang.String getDataVersion() {
		return get("dataVersion");
	}

	public M setDataCreateTime(java.util.Date dataCreateTime) {
		set("dataCreateTime", dataCreateTime);
		return (M)this;
	}

	public java.util.Date getDataCreateTime() {
		return get("dataCreateTime");
	}

	public M setDataUpdateTime(java.util.Date dataUpdateTime) {
		set("dataUpdateTime", dataUpdateTime);
		return (M)this;
	}

	public java.util.Date getDataUpdateTime() {
		return get("dataUpdateTime");
	}

	public M setDataDeleteTime(java.util.Date dataDeleteTime) {
		set("dataDeleteTime", dataDeleteTime);
		return (M)this;
	}

	public java.util.Date getDataDeleteTime() {
		return get("dataDeleteTime");
	}

}
