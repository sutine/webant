package org.webant.worker.processor;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.webant.commons.entity.HttpDataEntity;

import java.util.Collection;
import java.util.stream.Collectors;

public class HtmlPageProcessor<T extends HttpDataEntity> extends HttpPageProcessor<T> {
    protected Document doc = null;

    @Override
    public void parse(String content) {
        doc = Jsoup.parse(content, url().toExternalForm());
    }

    @Override
    public Collection<String> links() {
        return doc.select("a").stream()
                .map(a -> a.absUrl("href"))
                .filter(StringUtils::isNotBlank).distinct()
                .collect(Collectors.toList());
    }
}
