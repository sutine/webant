package org.webant.queen.web.link;

import com.jfinal.plugin.activerecord.Page;
import org.webant.queen.web.common.model.Link;

public class LinkService {

	private static final Link dao = new Link().dao();
	
	public Page<Link> paginate(int pageNumber, int pageSize) {
		return dao.paginate(pageNumber, pageSize, "select *", "from link order by id asc");
	}
	
	public Link findById(int id) {
		return dao.findById(id);
	}
	
	public void deleteById(int id) {
		dao.deleteById(id);
	}
}
