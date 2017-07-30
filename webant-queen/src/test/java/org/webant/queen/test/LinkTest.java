package org.webant.queen.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.webant.queen.commons.vo.ListResult;
import org.webant.queen.commons.vo.Response;
import org.webant.queen.link.entity.Link;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LinkTest {

    @LocalServerPort
    private int port = 8080;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testList() {
        Response<ListResult<Link>> response = restTemplate.getForObject("http://localhost:" + port + "/link/list", Response.class);
        Assert.assertTrue(response.getData().getList().size() == 20);
    }

    @Test
    public void testSave() {
        String url = "http://localhost:" + port + "/link/save";
        String id = "id11";
        Link entity = new Link();
        entity.setId(id);
        entity.setStatus(Link.LINK_STATUS_INIT);
        entity.setSiteId("siteId");
        entity.setTaskId("taskId");
        entity.setLastCrawlTime(new Date());

        HttpEntity<Link> requestEntity = new HttpEntity<>(entity);

        restTemplate.postForEntity(url, requestEntity, String.class);
    }

    @Test
    public void testSaveList() {
        String url = "http://localhost:" + port + "/link/save/list";
        Link entity = new Link();
        entity.setId("id");
        entity.setStatus(Link.LINK_STATUS_INIT);
        entity.setSiteId("siteId");
        entity.setTaskId("taskId");
        entity.setLastCrawlTime(new Date());

        Link entity1 = new Link();
        entity1.setId("id11");
        entity1.setStatus(Link.LINK_STATUS_INIT);
        entity1.setSiteId("siteId1");
        entity1.setTaskId("taskId1");
        entity1.setLastCrawlTime(new Date());

        List<Link> list = new LinkedList<>();
        list.add(entity);
        list.add(entity1);

        HttpEntity<List<Link>> requestEntity = new HttpEntity<>(list);

        restTemplate.postForEntity(url, requestEntity, String.class);
    }
}
