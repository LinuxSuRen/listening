/*
Copyright 2024 LinuxSuRen.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package listening.linuxsuren.github.io.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Podcast {
    private String name;
    private String rss;
    private String logoURL;
    private String link;
    private ZonedDateTime publishDate;
    private List<String> categories = new ArrayList<>();

    public Podcast() {}

    public Podcast(String name, String rss) {
        this.name = name;
        this.rss = rss;
    }

    public boolean isValid() {
        return name != null && !name.isEmpty() && rss != null && !rss.isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public int hashCode() {
        int code = 0;
        if (name != null ) {
            code += name.hashCode();
        }
        if (rss != null) {
            code += rss.hashCode();
        }
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return hashCode() == obj.hashCode();
    }
}
