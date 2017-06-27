package org.webant.queen.web.link;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import org.webant.queen.web.common.model.Link;

@Before(LinkInterceptor.class)
public class LinkController extends Controller {
	
	static LinkService service = new LinkService();
	
	public void index() {
		setAttr("blogPage", service.paginate(getParaToInt(0, 1), 10));
		render("link.html");
	}

	public void links() {
		Page<Link> links = service.paginate(getParaToInt(0, 1), 10);
		renderJson(links);
	}

	public void add() {
	}
	
	/**
	 * save 与 update 的业务逻辑在实际应用中也应该放在 serivce 之中，
	 * 并要对数据进正确性进行验证，在此仅为了偷懒
	 */
	@Before(LinkValidator.class)
	public void save() {
		getModel(Link.class).save();
		redirect("/link");
	}
	
	public void edit() {
		setAttr("link", service.findById(getParaToInt()));
	}

	@Before(LinkValidator.class)
	public void update() {
		getModel(Link.class).update();
		redirect("/link");
	}
	
	public void delete() {
		service.deleteById(getParaToInt());
		redirect("/link");
	}
}


